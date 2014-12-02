package com.ssof.exceptions;

public class DateFormatException extends Exception{
	private static final long serialVersionUID = 1612062193454095996L;
	
	private String data;
	private String descrizione;
	
	/**
	 * 
	 * @param data La stringa che non si e' riusciti a trasformare in GregorianCalendar
	 * @param descrizione Descrizione ulteriore del problema, oppure null.
	 */
	public DateFormatException(String data, String descrizione){
		this.data = data;
		this.descrizione = descrizione;
	}
	
	public String toString(){
		String ret = "Data '" + data + "' errata. Il formato deve essere gg/mm/aaaa oppure gg-mm-aaaa.";
		if(descrizione != null)
			ret += "\nUlteriore descrizione: " + descrizione;
		
		return ret;
	}

}
