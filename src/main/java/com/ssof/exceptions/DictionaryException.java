package com.ssof.exceptions;

public class DictionaryException extends Exception{

	private static final long serialVersionUID = 5392401365950105211L;
	private final String errdes;
	
	public DictionaryException(String errdes){
		this.errdes = errdes;
	}
	
	public String toString(){
		return errdes;
	}
}
