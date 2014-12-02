package com.ssof.exceptions;

public class WrongAttributeValueException extends Exception {
	private static final long serialVersionUID = -987552568698001242L;
	
	String file;
	int fileRow;
	int minRange;
	int maxRange;
	int valueRead;
	String attribute;
	
	/**
	 * 
	 * @param file File che si stava leggendo
	 * @param fileRow Riga del file dove si e' verificato l'errore
	 * @param minRange Valore minimo ammesso per l'attributo
	 * @param maxRange Valore massimo ammesso per l'attributo
	 * @param valueRead Valore incontrato
	 * @param attribute Attributo al quale si sarebbe dovuto assegnare il valore valueRead
	 */
	public WrongAttributeValueException(String file, int fileRow, int minRange, int maxRange, int valueRead, String attribute){
		this.file = file;
		this.fileRow = fileRow;
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.valueRead = valueRead;
		this.attribute = attribute;
	}
	
	public String toString(){
		return "Errore durante la lettura del file " + file + " alla riga " + fileRow + ". Ho incontrato il valore " + valueRead + " per l'attributo " + 
				attribute + ", ma i valori ammessi per questo attributo sono nel range [" + minRange + ", " + maxRange + "].";
	}

}
