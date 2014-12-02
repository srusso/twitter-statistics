package com.ssof.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import ts.datatypes.TimePeriod;
import ts.datatypes.WordTimelineTweets;
import ts.twitter.SingleTweet;

/**
 * Grafico uso parole.
 * @author Simone
 *
 */
public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{
	private static final long serialVersionUID = 235368751392888937L;
	
	/**
	 * Larghezza di ogni campionamento in pixel.
	 */
	private static final int SLICE_WIDTH = 20;
	
	private static final Color backgroundColor = Color.WHITE;
	private static final Color axisColor = Color.BLACK;
	private final Color [] lineColors;
	
	private final List <WordTimelineTweets> timelines;
	
	/**
	 * Periodo che va dal primo tweet all'ultimo tweet, precisamente.
	 */
	private final TimePeriod timePeriod;
	
	
	/**
	 * Periodo che va dal primo tweet all'ultimo tweet, dalla mezzanotte [appena dopo] del primo giorno alla mezzanotte [appena prima] dell'ultimo giorno.
	 */
	private final TimePeriod timePeriodDay;
	
	private final long timePeriodDayLengthInMillis;
	private final long timePeriodDayStartInMillis;
	
	
	/**
	 * Giorno del primo tweet, appena scattata la mezzanotte.
	 */
	private final GregorianCalendar startTime;
	
	/**
	 * Giorno dell'ultimo tweet, appena prima che scatti la mezzanotte.
	 */
	private final GregorianCalendar endTime;
	
	/**
	 * Se disegnare o no il grafico con un dettaglio nell'ordine delle ore.
	 */
	private final boolean drawHours;
	
	/**
	 * Se disegnare o no il grafico con un dettaglio nell'ordine dei minuti.
	 */
	private final boolean drawMinutes;
	
	/**
	 * Coordinate del mouse.
	 */
	private int mouseX = 0, mouseY = 0;
	
	public GraphPanel(List <WordTimelineTweets> timelines) {
		this.timelines = timelines;
		
		lineColors = new Color[4];
		lineColors[0] = Color.RED;
		lineColors[1] = Color.BLUE;
		lineColors[2] = Color.GREEN;
		lineColors[3] = Color.YELLOW;
		
		setPreferredSize(getInitialSize());
		setBackground(backgroundColor);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		
		timePeriod = getTimePeriod();

		startTime = new GregorianCalendar();
		endTime = new GregorianCalendar();
		startTime.setTimeInMillis(timePeriod.getStart());
		endTime.setTimeInMillis(timePeriod.getEnd());
		
		startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
		startTime.set(GregorianCalendar.MINUTE, 0);
		startTime.set(GregorianCalendar.SECOND, 0);
		startTime.set(GregorianCalendar.MILLISECOND, 0);
		
		endTime.set(GregorianCalendar.HOUR_OF_DAY, 23);
		endTime.set(GregorianCalendar.MINUTE, 59);
		endTime.set(GregorianCalendar.SECOND, 59);
		endTime.set(GregorianCalendar.MILLISECOND, 999);
		
		timePeriodDay = new TimePeriod(startTime, endTime);
		
		timePeriodDayLengthInMillis = timePeriodDay.lenghtInMillis();
		timePeriodDayStartInMillis  =  timePeriodDay.getStart();
		drawHours   = timePeriod.getNumberOfDays() < 3;
		drawMinutes = timePeriod.getNumberOfDays() == 0;
	}
	
	private static Dimension getInitialSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int size=(int)(screenSize.getHeight()*3)/4;
		Dimension d=new Dimension(size, size);	
		return d;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width  = getWidth();
		int height = getHeight();
		int originx, originy; //origine degli assi
		int endx, endy; //fine asse orizzontale e verticale
		int NUM_SLICES = width / SLICE_WIDTH;

		originx = width / 100;
		originy = (height * 99) / 100;
		endx = width*99/100;
		endy = height/100;
		
		//disegno gli assi cartesiani
		g2.setColor(axisColor);
		g2.drawLine(originx, originy, originx, endy); //asse orizzontale
		g2.drawLine(originx, originy, endx, originy); //asse verticale
		g2.drawLine(originx, endy, originx/2, endy*3/2);   //freccetta
		g2.drawLine(originx, endy, originx*3/2, endy*3/2); //nell'asse verticale
		g2.drawLine(endx, originy, endx - (width-endx)/2, originy - (height-originy)/2);   //freccetta
		g2.drawLine(endx, originy, endx - (width-endx)/2, originy + (height-originy)/2); //nell'asse orizzontale
		
		
		if(timePeriod == null){
			g2.drawString("NO TWEETS", width/2, height/2);
			return;
		}
		
		
		//ora disegno le linee sull'asse orizzontale [asse temporale]
		int startTimex = originx + (width-endx)/2 -5; //coordinata dell'inizio del periodo temporale sull'asse orizzontale [cioe' della riga del primo giorno]
		int endTimex = endx - (width-endx)/2 -5; //coordinata della fine del periodo temporale sull'asse orizzontale [cioe' della riga dell'ultimo giorno]
		int minuteLineHeight, hourLineHeight, dayLineHeight;
		int minuteLineDist, hourLineDist, dayLineDist; //distanza tra una linea e l'altra, in pixel
		int days = timePeriod.getNumberOfDays();
		
		dayLineDist    = (endTimex - startTimex) / days;
		hourLineDist   = dayLineDist / 24;
		minuteLineDist = hourLineDist / 60;
		
		dayLineHeight    = (height-originy) / 2;
		hourLineHeight   = dayLineHeight / 2;
		minuteLineHeight = hourLineHeight / 2;
		
		for(int i = 0 ; i <= days ; i++){
			g2.drawLine(startTimex + i * dayLineDist, originy - dayLineHeight * 5, startTimex + i * dayLineDist, originy + dayLineHeight); //disegno le linee dei giorni
			
			if(drawHours){
				
				for(int j = 1 ; j < 24 ; j++){
					
					g2.drawLine(startTimex + j * hourLineDist, originy - hourLineHeight * 5, startTimex + j * hourLineDist, originy + hourLineHeight); //disegno le linee delle ore
					
					if(drawMinutes){
						for(int k = 1 ; k < 60 ; k++){
							g2.drawLine(startTimex + j * hourLineDist + k * minuteLineDist,
									originy - minuteLineHeight * 5,
									startTimex + j * hourLineDist + k * minuteLineDist,
									originy + minuteLineHeight); //disegno le linee dei minuti
						}
					}
					
				}
				
			}
			
		}
		
		//disegno la data vicino al puntatore del mouse
		long mouseTime = timePeriodDay.getStart() + (mouseX - startTimex) * (timePeriodDayLengthInMillis) / (endTimex - startTimex);
		GregorianCalendar mouseTimeCal = new GregorianCalendar();
		mouseTimeCal.setTimeInMillis(mouseTime);
		int hour = mouseTimeCal.get(GregorianCalendar.HOUR_OF_DAY);
		int minute = mouseTimeCal.get(GregorianCalendar.MINUTE);
		int second = mouseTimeCal.get(GregorianCalendar.SECOND);
		String dateToDraw = "" +
				mouseTimeCal.get(GregorianCalendar.DAY_OF_MONTH)
				+ "/" +
				(mouseTimeCal.get(GregorianCalendar.MONTH) + 1)
				+ "/" + 
				mouseTimeCal.get(GregorianCalendar.YEAR)
				+ " ore " +
				((hour < 10)?("0"+hour):(""+hour))
				+ ":" +
				((minute < 10)?("0"+minute):(""+minute))
				+ ":" +
				((second < 10)?("0"+second):(""+second));
		
		FontMetrics metrics = g2.getFontMetrics(getFont());
		int hgt = metrics.getHeight();
		int wdt = metrics.stringWidth(dateToDraw);
		int tx = mouseX - wdt/2;
		
		if(tx < originx){
			tx = originx;
		} else if(tx + wdt > endx){
			tx = endx - wdt;
		}
		
		g2.drawString(dateToDraw, tx, mouseY - hgt);
		
		
		//ora disegno le linee che compongono il grafico, una per ogni timeline
		long timeSliceLength = timePeriodDay.lenghtInMillis() / NUM_SLICES;
		int [][] timeSliceHits = new int[timelines.size()][NUM_SLICES];
		int maxHits;
		
		//inizializzo hits
		for(int k = 0 ; k < timeSliceHits.length ; k++){
			for(int j = 0 ; j < timeSliceHits[0].length ; j++)
				timeSliceHits[k][j] = 0;
		}
		
		//calcolo hits per ogni timeline, per ogni time slice
		for(int i = 0 ; i < timelines.size() ; i++){

			WordTimelineTweets tl = timelines.get(i);
			Iterator <SingleTweet> j = tl.tweets.iterator(); 
			while(j.hasNext()){
				SingleTweet st = j.next();
				timeSliceHits[i][(int) ((st.millisSinceEpoch - timePeriodDayStartInMillis) / timeSliceLength) ]++;
			}
		
		}
		
		maxHits = getMax(timeSliceHits); //calcolo il massimo di hit globale per una singola time slice
		
		if(maxHits == 0)
			return;
		
		
		//finalmente, disegno
		for(int i = 0 ; i < timelines.size() ; i++){
			g2.setColor(lineColors[i]);

			for(int j = 1 ; j < timeSliceHits[i].length ; j++){
				g2.drawLine(startTimex + (j - 1) * SLICE_WIDTH,
						originy - (timeSliceHits[i][j - 1]) * (originy - endy) / maxHits,
						startTimex + j * SLICE_WIDTH,
						originy - (timeSliceHits[i][j]) * (originy - endy) / maxHits);
				
				int radius = 10;
				if(timeSliceHits[i][j-1] != 0)
					g2.fillOval(startTimex + (j - 1) * SLICE_WIDTH - radius/2, originy - (timeSliceHits[i][j - 1]) * (originy - endy) / maxHits - radius/2, radius, radius);
			}
		
		}
		
		//disegno i valori sull'asse y
		DecimalFormat df = new DecimalFormat("#.#");
		g2.setColor(Color.BLACK);

		for(int i = 1 ; i <= 10 ; i++){
			String stringToDraw = "" + df.format((maxHits*i/10));
			int h = originy - ((originy-endy)/10)*i;

			if(i<10)g2.drawLine(originx/2, h, originx+originx/2, h);
			g2.drawString(stringToDraw, originx*2, h+hgt/2);
		}

		
	}
	
	private int getMax(int[][] timeSliceHits) {
		int max = timeSliceHits[0][0];
		
		for(int i = 0 ; i < timeSliceHits.length ; i++){
			for(int j = 0 ; j < timeSliceHits[0].length ; j++){
				if(max < timeSliceHits[i][j])
					max = timeSliceHits[i][j];
			}
		}
		
		return max;
	}

	/**
	 * Ritorna un periodo di tempo che inizia con il tweet piu' vecchio e finisce con il tweet piu recente.
	 * Serve per capire quale periodo temporale verra' disegnato dal grafico.
	 * @return Un'istanza di TimePeriod
	 */
	private TimePeriod getTimePeriod(){
		long minStart = Long.MAX_VALUE;
		long maxEnd   = 0;
		
		for(int i = 0 ; i < timelines.size() ; i++){
			
			WordTimelineTweets tl = timelines.get(i); //di ogni timeline...
			
			if(tl.tweets.size() == 0)
				continue;
			
			SingleTweet firstTweet = tl.getOldestTweet(); //...prendo il tweet piu vecchio
			SingleTweet lastTweet  = tl.getNewestTweet(); //e il tweet piu recente
			
			if(firstTweet.millisSinceEpoch < minStart)
				minStart = firstTweet.millisSinceEpoch;
				
			if(lastTweet.millisSinceEpoch > maxEnd)
				maxEnd = lastTweet.millisSinceEpoch;
			
		}
		
		if(minStart == Long.MAX_VALUE || maxEnd == 0) //nessun tweet!
			return null;
		
		else return new TimePeriod(minStart, maxEnd);
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}

	public void componentHidden(ComponentEvent arg0) {
		
	}
	
	public void componentMoved(ComponentEvent arg0) {
		
	}

	public void componentResized(ComponentEvent arg0) {
		repaint();
	}

	public void componentShown(ComponentEvent arg0) {
		
	}
}
