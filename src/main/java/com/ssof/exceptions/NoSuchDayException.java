package com.ssof.exceptions;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NoSuchDayException extends Exception {

	private static final long serialVersionUID = 3038628986783327194L;

	private final Calendar day;
	
	public NoSuchDayException(Calendar day){
		this.day = day;
	}
	
	public String toString(){
		return "Nessun dato per il giorno specificato [" + "" +
				day.get(GregorianCalendar.DAY_OF_MONTH)
				+ "/" +
				(day.get(GregorianCalendar.MONTH) + 1)
				+ "/" + 
				day.get(GregorianCalendar.YEAR) + 
				"]";
	}
}
