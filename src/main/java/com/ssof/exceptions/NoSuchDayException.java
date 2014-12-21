package com.ssof.exceptions;

import org.joda.time.DateTime;

public class NoSuchDayException extends Exception {

	private static final long serialVersionUID = 3038628986783327194L;

	private final DateTime day;
	
	public NoSuchDayException(DateTime day){
		this.day = day;
	}
	
	public String toString(){
		return "Nessun dato per il giorno specificato [" + "" +
				day.getDayOfMonth()
				+ "/" +
				day.getMonthOfYear()
				+ "/" + 
				day.getYear() + 
				"]";
	}
}
