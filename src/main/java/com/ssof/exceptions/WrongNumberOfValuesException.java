package com.ssof.exceptions;

public class WrongNumberOfValuesException extends Exception{
	private static final long serialVersionUID = 7104615156672635963L;
	
	int numValuesRead;
	int numValuesExpected;
	int fileRow;
	String file;

	/**
	 * 
	 * @param numValuesRead Numero valori letti per una certa parola
	 * @param numValuesExpected Numero di valori che ci si aspettava di leggere
	 * @param file File che si stava leggendo
	 * @param fileRow Riga dove e' stato incontrato l'errore
	 */
	public WrongNumberOfValuesException(int numValuesRead, int numValuesExpected, String file, int fileRow){
		this.numValuesExpected = numValuesExpected;
		this.numValuesRead = numValuesRead;
		this.fileRow = fileRow;
		this.file = file;
	}
	
	public String toString(){
		return "Mi aspettavo " + numValuesExpected + " attributi, ma nella riga " + fileRow + " del file " + file + " ne ho trovati " + numValuesRead;
	}
}
