package com.ssof.gui;

import com.ssof.datatypes.MoodData;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

/**
 * JFrame che mostra il grafico dell'andamento delle varie emozioni nel tempo.
 * @author Simone
 *
 */
public class MoodGraphFrame extends JFrame implements ItemListener{
	private static final long serialVersionUID = 8286729303018857702L;
	private final MoodGraphPanel graphArea;
	
	private java.awt.List scrollList;
	
	/**
	 * Crea un nuovo grafico con le timeline specificate e lo mostra.
	 * @param parent
	 * @param timelines
	 */
	public MoodGraphFrame(MoodData mdata) throws RuntimeException{		
		String title = "Emozioni";
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setTitle(title);
		
		Container container = getContentPane();
		
		graphArea = new MoodGraphPanel(mdata);
		graphArea.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JTextPane txtpnSelezionareAttributo = new JTextPane();
		txtpnSelezionareAttributo.setText("Selezionare attributo:");
		txtpnSelezionareAttributo.setEditable(false);

		scrollList = new java.awt.List();
		scrollList.setMultipleMode(true);
		for(String a : mdata.getValidAttributes())
			scrollList.add(a);
		
		scrollList.addItemListener(this);
		
		panel.add(txtpnSelezionareAttributo);
		panel.add(scrollList);
		
		container.add(graphArea, BorderLayout.CENTER);
		container.add(panel, BorderLayout.NORTH);
		
		graphArea.repaint();
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void itemStateChanged(ItemEvent event){
		graphArea.setSelectedAttributes(scrollList.getSelectedItems());
	}

}
