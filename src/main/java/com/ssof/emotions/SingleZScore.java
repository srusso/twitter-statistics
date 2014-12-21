package com.ssof.emotions;

import com.ssof.datatypes.TimePeriod;
import org.joda.time.DateTime;


public class SingleZScore{
	public TimePeriod timeInterval;
	
	/**
	 * Come M, per� calcolata solo sui tweet pubblicati nell'intervallo
	 * specificato dal campo TimePeriod timeInterval di questa classe.
	 */
	public double [] X;
	
	public double [] Z;
	
	/**
	 * Numero di tweet nell'intervallo specificato.
	 */
	public long tweetsInInterval;
	
	public double [] Xtot;
	
	/**
	 * 0 == domenica
	 * ..
	 * ..
	 * 6 == sabato
	 */
	public int dayOfWeek;
	
	public SingleZScore(Dictionary dictionary, TimePeriod t){
		X   = new double [dictionary.attributes.length];
		Z   = new double [dictionary.attributes.length];
		
		Xtot   = new double [dictionary.attributes.length];
		
		tweetsInInterval = 0;
		timeInterval = t;
		
		
		for(int i = 0 ; i < Xtot.length ; i++){
			Xtot[i] = 0;
		}
		
		
		DateTime cal = new DateTime(t.getEnd());
		dayOfWeek = cal.getDayOfWeek() - 1;
	}
}
