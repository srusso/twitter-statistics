package com.ssof.exceptions;

/**
 * Eccezione lanciata dalla classe Dictionary quando si cerca di
 * calcolare il mood di un tweet che non contiene nessuna parola
 * del lessico.
 * @author Simone
 *
 */
public class NoMoodException extends Exception {

	private static final long serialVersionUID = 3707609599817773821L;
	
	public NoMoodException(String descr){
		super(descr);
	}

}
