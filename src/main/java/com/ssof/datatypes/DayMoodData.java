package com.ssof.datatypes;

import com.ssof.emotions.Dictionary;

/**
 * Contiene la media delle emozioni per un singolo giorno.
 * @author Simone
 *
 */
public class DayMoodData{
	
	/**
	 * Media delle emozioni per singolo tweet, per il giorno specificato dal campo day.
	 */
	public double [] mean;
	
	private double [] total;
	
	private final Dictionary dictionary;
	
	private int numberOfTweets;
	
	public DayMoodData(Dictionary dictionary) throws Exception{
		this.dictionary     = dictionary;
		this.numberOfTweets = 0;
		this.total 			= null;	
	}
	
	/**
	 * Calcola la media dei mood, avendo array contenente il totale e il numero di tweet.
	 * @throws Exception
	 */
	public void setMean() throws Exception{
		if(numberOfTweets == 0)
			throw new Exception("Numero di tweets == 0!");
		
		this.mean = ArrayMath.arrayDivide(this.total, numberOfTweets);		
	}

	
	/**
	 * Aggiorna l'array che contiene la somma dei mood dei tweet del giorno.
	 * @param arrayToAdd
	 * @throws Exception
	 */
	public void updateTotal(double [] arrayToAdd) throws Exception{		
		if(dictionary.getAttributeArray().length != arrayToAdd.length)
			throw new Exception("DayMoodData exception 1 in DayMoodData.updateTotal()");
		
		if(total == null){
			total = arrayToAdd;
		} else {
			if(arrayToAdd.length != total.length)
				throw new Exception("DayMoodData exception 2 in DayMoodData.updateTotal()");
			
			for(int i = 0 ; i < arrayToAdd.length ; i++)
				total[i] += arrayToAdd[i];
		}
		
		numberOfTweets++;
	}
}
