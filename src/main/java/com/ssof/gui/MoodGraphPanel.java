package com.ssof.gui;

import com.ssof.datatypes.MoodData;
import com.ssof.exceptions.NoSuchAttributeException;
import com.ssof.exceptions.NoSuchDayException;
import com.ssof.utils.comparators.DateComparator;
import org.joda.time.DateTime;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafico che mostra il grafico dell'andamento delle varie emozioni nel tempo.
 * @author Simone
 *
 */
public class MoodGraphPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{
	private static final long serialVersionUID = 235368751392888937L;

	
	private static final Color backgroundColor = Color.WHITE;
	private static final Color axisColor = Color.BLACK;
	private final List <Color> lineColors;
	
	/**
	 * Coordinate del mouse.
	 */
	private int mouseX = 0, mouseY = 0;
	
	/**
	 * Contiene i dati che servono per poter disegnare il grafico.
	 */
	private MoodData mdata;
	
	private DateTime [] days;
	
	private String [] selectedAttributes;
	
	public MoodGraphPanel(MoodData mdata) {
		this.mdata = mdata;
		
		lineColors = new ArrayList<>();
		lineColors.add(Color.RED);
		lineColors.add(Color.BLUE);
		lineColors.add(Color.CYAN);
		lineColors.add(Color.GRAY);
		lineColors.add(Color.GREEN);
		lineColors.add(Color.MAGENTA);
		lineColors.add(Color.ORANGE);
		lineColors.add(Color.PINK);
		lineColors.add(Color.YELLOW);
		lineColors.add(Color.DARK_GRAY);
		lineColors.add(Color.BLACK);
		while(lineColors.size() < mdata.getValidAttributes().size()){
			lineColors.addAll(lineColors);
		}
		
		setPreferredSize(getInitialSize());
		setBackground(backgroundColor);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		
		List<DateTime> dayList = new ArrayList <>();
		dayList.addAll(mdata.getValidDays());
		Collections.sort(dayList, new DateComparator());
		days = dayList.toArray(new DateTime[dayList.size()]);
		
		selectedAttributes = null;
		
		JOptionPane.showMessageDialog(null, "Percentuale di tweet analizzabili con il lessico fornito: " + mdata.getEvaluedPercentage());
	}
	
	public void setSelectedAttributes(String [] attrs){
		selectedAttributes = attrs;
		repaint();
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
		g2.drawLine(endx, originy, endx - (width-endx)/2, originy - (height-originy)/2);  //freccetta
		g2.drawLine(endx, originy, endx - (width-endx)/2, originy + (height-originy)/2); //nell'asse orizzontale
		
		
		if(days.length == 0){
			g2.drawString("NO TWEETS", width/2, height/2);
			return;
		}
		
		if(selectedAttributes == null){
			g2.drawString("Selezionare un attributo", width/2, height/2);
			return;
		}
		
		
		//ora disegno le linee sull'asse orizzontale [asse temporale]
		int startTimex = originx ; //coordinata dell'inizio del periodo temporale sull'asse orizzontale [cioe' della riga del primo giorno]
		int endTimex = endx - (width-endx)/2; //coordinata della fine del periodo temporale sull'asse orizzontale [cioe' della riga dell'ultimo giorno]
		int dayLineHeight;
		int dayLineDist; //distanza tra una linea e l'altra, in pixel
		
		dayLineDist   = (endTimex - startTimex) / days.length;
		dayLineHeight = (height-originy) / 2;
		
		for(int i = 1 ; i <= days.length ; i++){
			//disegno le linee dei giorni, sull'asse delle ascisse
			g2.drawLine(startTimex + i * dayLineDist, originy - dayLineHeight * 5, startTimex + i * dayLineDist, originy + dayLineHeight);			
		}	
		
		double max_y_draw = 0;
		for (String selectedAttribute : selectedAttributes) {
			//passo al calcolo dei valori da disegnare
			double[] values = new double[days.length];
			double maxValue = 0; //valore massimo sull'asse delle ordinate

			//prendo i valori da disegnare sull'asse delle ordinate (cioe' la media dell'attributo selezionato per ogni giorno)
			for (int j = 0; j < values.length; j++) {
				try {
					values[j] = mdata.getDayMean(days[j], selectedAttribute);
				} catch (NoSuchDayException | NoSuchAttributeException e) {
					System.out.println(e);
					return;
				}

				if (maxValue < values[j]) {
					maxValue = values[j];
					if (maxValue > max_y_draw)
						max_y_draw = maxValue;
				}
			}

			//finalmente, disegno il grafico
			try {
				g2.setColor(lineColors.get(mdata.getAttributePosition(selectedAttribute)));
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(null, "Impossibile mostrare grafico di \'" + selectedAttribute + "\'.", "Errore " +
					"interno", JOptionPane.ERROR_MESSAGE);
			}

			for (int j = 1; j < values.length; j++) {
				int h1 = (int) (values[j - 1] * (originy - endy) / maxValue);
				int h2 = (int) (values[j] * (originy - endy) / maxValue);
				g2.drawLine(startTimex + (j - 1) * dayLineDist + dayLineDist / 2, originy - h1, startTimex + j * dayLineDist + 
					dayLineDist / 2, originy - h2);
			}
		}
		
		//disegno i valori sull'asse y
		if(selectedAttributes.length>0){
			DecimalFormat df = new DecimalFormat("#.#");
			g2.setColor(Color.BLACK);
			
			FontMetrics metrics = g2.getFontMetrics(getFont());
			int hgt = metrics.getHeight();
			
			for(int i = 1 ; i <= 10 ; i++){
				String stringToDraw = "" + df.format((max_y_draw*i/10));
				int h = originy - ((originy-endy)/10)*i;
				
				if(i<10)g2.drawLine(originx/2, h, originx+originx/2, h);
				g2.drawString(stringToDraw, originx*2, h+hgt/2);
			}
		}
		
		
		g2.setColor(axisColor);
		//disegno la data vicino al puntatore del mouse
		if(mouseX < endx){
			int dayLenPx = (endx - originx) / days.length;
			int d = (mouseX - originx) / dayLenPx;
		
			if(d < days.length){
				String dateToDraw = "Giorno " + days[d].getDayOfMonth()
						+ "/" +
						(days[d].getMonthOfYear())
						+ "/" + 
						days[d].getYear();

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
			}
		}
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
