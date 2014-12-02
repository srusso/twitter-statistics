package com.ssof.exceptions;

public class WrongExpansionParametersException extends RuntimeException {

	private static final long serialVersionUID = -6202802623225956507L;

	public WrongExpansionParametersException(String descr){
		super(descr);
	}
	
}
