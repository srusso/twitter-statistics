package com.ssof.gui;

import com.ssof.emotions.ZScoreForGraph;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class ZScoreGraphFrame extends JFrame implements ItemListener{

	private static final long serialVersionUID = 9085061390672540093L;
	
	private final ZScoreGraphPanel graphArea;
	private java.awt.List scrollList;
	
	public ZScoreGraphFrame(ZScoreForGraph zscores){
		
		String title = "Z-scores";
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setTitle(title);
		
		Container container = getContentPane();
		
		graphArea = new ZScoreGraphPanel(zscores);
		graphArea.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JTextPane txtpnSelezionareAttributo = new JTextPane();
		txtpnSelezionareAttributo.setText("Selezionare attributo:");
		txtpnSelezionareAttributo.setEditable(false);

		scrollList = new java.awt.List();
		scrollList.setMultipleMode(true);
		for(String a : zscores.getValidAttributes())
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
