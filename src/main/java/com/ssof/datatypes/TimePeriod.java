package com.ssof.datatypes;

import org.joda.time.DateTime;

import java.util.Date;

public class TimePeriod {
	private final long periodStart;
	private final long periodEnd;
	private final int numberOfDays;
	private final int numberOfHours;
	
	private final static int MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;
	
	public TimePeriod(long start, long end){
		periodStart = start;
		periodEnd   = end;
		
		numberOfDays  = 1 + (int) ( (end - start) / MILLIS_IN_A_DAY );
		numberOfHours = numberOfDays * 24;
	}
	
	public TimePeriod(DateTime start, DateTime end){
		this(start.getMillis(), end.getMillis());
	}
	
	public boolean equals(Object o){
		if(o == null || o.getClass()!=this.getClass())
			return false;
		
		TimePeriod x = (TimePeriod) o;
		
		return x.getStart() == this.getStart() && x.getEnd() == this.getEnd();
	}
	
	/**
	 * Ritorna true se la data specificata e' contenuta in
	 * questo TimePeriod.
	 * @param date
	 * @return
	 */
	public boolean contains(long date){
		return date >= periodStart && date <= periodEnd; 
	}
	
	/**
	 * Ritorna true se la data specificata e' contenuta in
	 * questo TimePeriod.
	 * @param date
	 * @return
	 */
	public boolean contains(DateTime date){
		long time = date.getMillis();
		return time >= periodStart && time <= periodEnd; 
	}
	
	/**
	 * Ritorna true se la data specificata e' contenuta in
	 * questo TimePeriod.
	 * @param date
	 * @return
	 */
	public boolean contains(Date date){
		long time = date.getTime();
		return time >= periodStart && time <= periodEnd; 
	}
	
	/**
	 * Ritorna il numero di giorni tra periodStart e periodEnd.
	 * @return
	 */
	public int getNumberOfDays(){
		return numberOfDays;
	}
	
	/**
	 * Ritorna il numero di ore tra periodStart e periodEnd.
	 * @return
	 */
	public int getNumberOfHours(){
		return numberOfHours;
	}
	
	/**
	 * Ritorna true se this inizia prima o insieme a tp AND this finisce dopo o insieme a tp.
	 * Ritorna false altrimenti.
	 * In altre parole ritorna true se e solo se this "contiene" tp. 
	 * @param tp TimePeriod con cui fare il confronto
	 * @return true o false
	 */
	public boolean contains(TimePeriod tp){
		return this.periodStart <= tp.periodStart && this.periodEnd >= tp.periodEnd;
	}
	
	/**
	 * Ritorna true se this inizia prima di tp.
	 * @param tp
	 * @return
	 */
	public boolean startsBefore(TimePeriod tp){
		return this.periodStart <= tp.periodStart;
	}
	
	/**
	 * Ritorna true se this finisce dopo di tp.
	 * @param tp
	 * @return
	 */
	public boolean endsAfter(TimePeriod tp){
		return this.periodEnd >= tp.periodEnd;
	}
	
	public long lenghtInMillis(){
		return periodEnd - periodStart;
	}

	public long getStart() {
		return periodStart;
	}

	public long getEnd() {
		return periodEnd;
	}
	
	public String toString(){
		DateTime start = new DateTime(periodStart);
		DateTime end   = new DateTime(periodEnd);
		
		String repr;
		int x = start.getDayOfMonth();
		repr  = ((x<10)?("0"+x):(""+x)) + "/";
		x = start.getMonthOfYear();
		repr += ((x<10)?("0"+x):(""+x)) + "/";
		x = start.getYear();
		repr += ((x<10)?("0"+x):(""+x)) + " - ";
		
		x = end.getDayOfMonth();
		repr += ((x<10)?("0"+x):(""+x)) + "/";
		x = end.getMonthOfYear();
		repr += ((x<10)?("0"+x):(""+x)) + "/";
		x = end.getYear();
		repr += (x<10)?("0"+x):(""+x);
		
		
		return repr;
	}
}
