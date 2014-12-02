package com.ssof.exceptions;

public class DictionaryFileFormatException extends Exception {
	private static final long serialVersionUID = 3767658530864636263L;
	private final String errdes;
	
	public DictionaryFileFormatException(String errdes){
		this.errdes = errdes;
	}
	
	public String toString(){
		return errdes;
	}
}
