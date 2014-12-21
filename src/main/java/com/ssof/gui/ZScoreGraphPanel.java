package com.ssof.gui;

import com.ssof.emotions.ZScoreForGraph;
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

public class ZScoreGraphPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{

	private static final long serialVersionUID = -1441820373172173708L;
	
	private final ZScoreForGraph zscores;
	
	
	private static final Color backgroundColor = Color.WHITE;
	private static final Color axisColor = Color.BLACK;
	private final List <Color> lineColors;
	
	/**
	 * Coordinate del mouse.
	 */
	private int mouseX = 0, mouseY = 0;
	
	private DateTime [] days;
	
	private String [] selectedAttributes;
	
	public ZScoreGraphPanel(ZScoreForGraph zscores){
		this.zscores = zscores;
		
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
		while(lineColors.size() < zscores.getValidAttributes().size()){
			lineColors.addAll(lineColors);
		}
		
		setPreferredSize(getInitialSize());
		setBackground(backgroundColor);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		
		List<DateTime> dayList = new ArrayList<>();
		dayList.addAll(zscores.getValidDays());
		Collections.sort(dayList, new DateComparator());
		days = dayList.toArray(new DateTime[dayList.size()]);
		
		selectedAttributes = null;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width  = getWidth();
		int height = getHeight();
		int originx; //origine degli assi
		int endx; //fine asse orizzontale e verticale
		int zeroLine = height /2;
		int verticalHeight = height / 4;
		int strwidth;
		
		FontMetrics metrics = g2.getFontMetrics(getFont());
		strwidth = metrics.stringWidth("-1.000");
		originx = width / 100 + strwidth;
		endx = width*99/100;

		
		//disegno gli assi cartesiani
		g2.setColor(axisColor);
		g2.drawLine(originx, zeroLine, endx, zeroLine); //asse orizzontale centrale
		g2.drawLine(originx, zeroLine+verticalHeight, endx, zeroLine+verticalHeight); //asse orizzontale inferiore
		g2.drawLine(originx, zeroLine-verticalHeight, endx, zeroLine-verticalHeight); //asse orizzontale superiore
		g2.drawLine(originx, zeroLine+verticalHeight, originx, zeroLine-verticalHeight); //asse verticale
		
		
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
		int dayLineDist; //distanza tra una linea e l'altra, in pixel
		
		dayLineDist   = (endTimex - startTimex) / days.length;
		
		double max_y_draw = Double.MIN_VALUE;
		for (String selectedAttribute : selectedAttributes) {
			//passo al calcolo dei valori da disegnare
			double[] values = new double[days.length];
			double maxValue = Double.MIN_VALUE; //valore massimo sull'asse delle ordinate

			//prendo i valori da disegnare sull'asse delle ordinate (cioe' la media dell'attributo selezionato per ogni giorno)
			for (int j = 0; j < values.length; j++) {
				try {
					values[j] = zscores.getDayValue(days[j], selectedAttribute);
				} catch (NoSuchDayException | NoSuchAttributeException e) {
					System.out.println(e);
					return;
				}

				if (values[j] >= 0) {
					if (maxValue < values[j]) {
						maxValue = values[j];
						if (maxValue > max_y_draw)
							max_y_draw = maxValue;
					}
				} else {
					double temp = -values[j];
					if (maxValue < temp) {
						maxValue = temp;
						if (maxValue > max_y_draw)
							max_y_draw = maxValue;
					}
				}
			}
		}

		for (String selectedAttribute : selectedAttributes) {
			//passo al calcolo dei valori da disegnare
			double[] values = new double[days.length];

			//prendo i valori da disegnare sull'asse delle ordinate (cioe' la media dell'attributo selezionato per ogni giorno)
			for (int j = 0; j < values.length; j++) {
				try {
					values[j] = zscores.getDayValue(days[j], selectedAttribute);
				} catch (NoSuchDayException e) {
					System.out.println(e);
					return;
				} catch (NoSuchAttributeException e) {
					System.out.println(e);
					return;
				}
			}

			//finalmente, disegno il grafico
			try {
				g2.setColor(lineColors.get(zscores.getAttributePosition(selectedAttribute)));
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(null, "Impossibile mostrare grafico di \'" + selectedAttribute + "\'.", "Errore " +
					"interno", JOptionPane.ERROR_MESSAGE);
			}

			for (int j = 1; j < values.length; j++) {
				int h1 = (int) (zeroLine - (values[j - 1] * verticalHeight / max_y_draw));
				int h2 = (int) (zeroLine - (values[j] * verticalHeight / max_y_draw));
				g2.drawLine(startTimex + (j - 1) * dayLineDist + dayLineDist / 2, h1, startTimex + j * dayLineDist + dayLineDist
					/ 2, h2);
			}
		}
		
		
		
		//disegno i valori sull'asse y
		DecimalFormat df = new DecimalFormat("#.###");
		g2.setColor(Color.BLACK);
		int hgt = metrics.getHeight();
		g2.drawString(" " + df.format(max_y_draw), (originx - strwidth)/2, zeroLine - verticalHeight + hgt/2);
		g2.drawString(" 0.0", (originx - strwidth)/2, zeroLine + hgt/2);
		g2.drawString("-" + df.format(max_y_draw), (originx - strwidth)/2, zeroLine + verticalHeight + hgt/2);
		
		
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
