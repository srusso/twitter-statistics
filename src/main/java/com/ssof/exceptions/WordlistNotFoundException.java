package com.ssof.exceptions;

public class WordlistNotFoundException extends Exception{

	private static final long serialVersionUID = 1495516571667357653L;
	private String path;
	
	/**
	 * 
	 * @param path Il percorso del file wordlist non trovato.
	 */
	public WordlistNotFoundException(String path){
		this.path = path;
	}
	
	public String toString(){
		return "Ho cercato la lista delle parole italiane nel file \"" + path + "\" ma non ho trovato tale file.";
	}

}
