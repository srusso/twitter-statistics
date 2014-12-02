package com.ssof.exceptions;

public class MultipleWordsException extends Exception{

	private static final long serialVersionUID = -566006855184046872L;
	
	String errorElement;


	public MultipleWordsException(String errorElement){
		this.errorElement = errorElement;
	}
	
	public String toString(){
		return "Uno degli elementi della lista di parole contiene piu' di una parola. Elemento: \'" + errorElement + "\'.";
	}

}
