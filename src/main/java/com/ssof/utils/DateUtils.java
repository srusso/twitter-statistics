package com.ssof.utils;

import com.ssof.exceptions.DateFormatException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {
	private final static DateTimeFormatter FORMATTER_DASH = DateTimeFormat.forPattern("dd-MM-yyyy");
	private final static DateTimeFormatter FORMATTER_SLASH = DateTimeFormat.forPattern("dd/MM/yyyy");
	
	/**
	 * Trasforma la data specificata dal parametro in una istanza della classe DateTime
	 * @param date La data, in formato gg/mm/aaaa o gg-mm-aaaa
	 * @return Una istanza di DateTime che rappresenta la data specificata dal parametro date
	 * @throws DateFormatException Se la data e' in un formato non riconosciuto o specifica una data non valida [ad esempio 30 febbraio]
	 */
	public DateTime translateStringDate(String date) throws DateFormatException {
		try {
			return FORMATTER_DASH.parseDateTime(date);
		} catch (IllegalArgumentException e) {
			
		}

		try {
			return FORMATTER_SLASH.parseDateTime(date);
		} catch (IllegalArgumentException e) {

		}
		
		throw new DateFormatException(date, "Invalid date format");
	}
}
