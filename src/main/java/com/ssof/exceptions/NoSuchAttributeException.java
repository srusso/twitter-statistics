package com.ssof.exceptions;


public class NoSuchAttributeException extends Exception {

	private static final long serialVersionUID = -5457506977419106816L;
	
	private final String attr;
	
	public NoSuchAttributeException(String attribute){
		this.attr = attribute;
	}
	
	public String toString(){
		return "Attributo \'" + attr + "\' non presente nel lessico";
	}
}
