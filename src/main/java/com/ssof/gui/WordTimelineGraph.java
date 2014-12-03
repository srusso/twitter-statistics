package com.ssof.gui;

import com.ssof.datatypes.WordTimelineTweets;

import javax.swing.JFrame;

import javax.swing.border.EmptyBorder;
import java.util.List;

/**
 * JFrame che mostra il grafico dell'utilizzo di alcune parole in un certo intervallo temporale.
 * @author Simone
 *
 */
public class WordTimelineGraph extends JFrame{
	private static final long serialVersionUID = -162966239202679047L;
	
	private final GraphPanel graphArea;
	
	/**
	 * Crea un nuovo grafico con le timeline specificate e lo mostra.
	 * @param parent
	 * @param timelines
	 */
	public WordTimelineGraph(MainWindow parent, List<WordTimelineTweets> timelines) throws RuntimeException{
		if(timelines.size() > 4)
			throw new RuntimeException("Numero massimo di timelines per grafico pari a 4, impossibile creare un grafico con " + timelines.size() + " timeline.");
		
		
		String title = "Timeline";
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		if(timelines.size() == 0){
			dispose();
		} else if(timelines.size() == 1){
			title += " parola " + "\"" +  timelines.get(0).word + "\"";
		} else {
			title += " parole: ";
			
			for(int i = 0 ; i < timelines.size() ; i++){
				if(i == 0) title += "\"" +  timelines.get(i).word + "\"";
				else title += ", \"" +  timelines.get(i).word + "\"";
			}
		}
		
		setTitle(title);
		
		graphArea = new GraphPanel(timelines);
		graphArea.setBorder(new EmptyBorder(2, 2, 2, 2));
		getContentPane().add(graphArea);
		
		graphArea.repaint();
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

}
