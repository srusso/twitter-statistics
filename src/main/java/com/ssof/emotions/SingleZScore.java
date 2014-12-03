package com.ssof.emotions;

import com.ssof.datatypes.TimePeriod;

import java.util.Calendar;
import java.util.GregorianCalendar;


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
		
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(t.getEnd());
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
	}
}
