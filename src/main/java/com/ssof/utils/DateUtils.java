package com.ssof.utils;

import com.ssof.exceptions.DateFormatException;

import java.util.GregorianCalendar;

public class DateUtils {
	/**
	 * Trasforma la data specificata dal parametro in una istanza della classe GregorianCalendar
	 * @param date La data, in formato gg/mm/aaaa o gg-mm-aaaa
	 * @return Una istanza di GregorianCalendar che rappresenta la data specificata dal parametro date
	 * @throws DateFormatException Se la data e' in un formato non riconosciuto o specifica una data non valida [ad esempio 30 febbraio]
	 */
	public static GregorianCalendar translateStringDate(String date) throws DateFormatException{
		GregorianCalendar control;
		int day, month, year, maxDay;
		String div = null;
		
		if(date.contains("/"))
			div = "/";
		else if(date.contains("-"))
			div = "-";
		else throw new DateFormatException(date, null);
		
		String [] pieces = date.split(div);
		
		if(pieces.length != 3)
			throw new DateFormatException(date, null);
		
		//prendo giorno del mese
		try{
			day = new Integer(pieces[0]);
			if(day < 1 || day > 31)
				throw new DateFormatException(date, "Il giorno deve essere compreso tra 1 e 31");
		} catch(NumberFormatException e){
			throw new DateFormatException(date, "La data contiene caratteri che non sono numeri");
		}
		
		//mese
		try{
			month = new Integer(pieces[1]);
			if(month < 1 || month > 12)
				throw new DateFormatException(date, "Il mese deve essere compreso tra 1 e 12");
		} catch(NumberFormatException e){
			throw new DateFormatException(date, "La data contiene caratteri che non sono numeri");
		}
		
		//e anno
		try{
			year = new Integer(pieces[2]);
		} catch(NumberFormatException e){
			throw new DateFormatException(date, "La data contiene caratteri che non sono numeri");
		}
		
		//controllo se il giorno del mese specificato e' valido
		control = new GregorianCalendar(year, month-1, 1);
		maxDay  = control.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		if(day > maxDay){
			throw new DateFormatException(date, "Hai specificato come data " + day + "/" + month + "/" + year + ", ma il mese " + month + " dell'anno " + year +  " ha " + maxDay + " giorni.");
		}
		
		return new GregorianCalendar(year, month-1, day); //I mesi partono da 0 in GregorianCalendar
	}
}
