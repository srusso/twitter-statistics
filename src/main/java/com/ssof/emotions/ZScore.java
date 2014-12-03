package com.ssof.emotions;

import com.ssof.datatypes.TimePeriod;
import com.ssof.exceptions.ZScoreException;
import com.ssof.twitter.SingleTweet;

import java.text.DecimalFormat;
import java.util.Collection;

public class ZScore {
	private Dictionary dictionary;
	private Collection<SingleTweet> tweets;
	private TimePeriod timeInterval;
	
	/**
	 * Ogni elemento di questo array contiene la media per tweet dei valori
	 * di un attributo del lessico, calcolata su tutti i tweet.
	 * Ad esempio se il primo attributo del lessico � "calma",
	 * il valore M[0] = 5.6 indica che in media ogni tweet ha una calma pari a 5.6.
	 */
	private double [] M;
	
	/**
	 * Come M, per� calcolata solo sui tweet pubblicati nell'intervallo
	 * specificato dal campo TimePeriod timeInterval di questa classe.
	 */
	private double [] X;
	
	/**
	 * Valore atteso E(X^2) del quadrato dei valori.
	 * Calcolato su tutti i tweet.
	 * M � E(X), EX2 � E(X^2).
	 */
	private double [] EM2;
	
	private double [] Z;
	
	/**
	 * Numero di tweet nell'intervallo specificato.
	 */
	long tweetsInInterval;
	
	/**
	 * Numero di tweet totali considerati.
	 */
	long totalTweets;
	
	public ZScore(Dictionary d, Collection <SingleTweet> tweets, TimePeriod ti) throws ZScoreException {
		this.dictionary   = d;
		this.tweets       = tweets;
		this.timeInterval = ti;
		
		computeZScore();
	}
	
	/**
	 * Input: Lessico (dictionary), insieme di tweet (tweets), intervallo di tempo (timeInterval)
	 * Output: gli array M, X, EX2, Z
	 * Pseudocodice:
	 * 		per ogni tweet
	 * 			aggiorna Mtot, EX2tot
	 * 			IF tweet IN time interval THEN aggiorna Xtot
	 * 
	 * 		calcola M, EX2 [E(X^2)], X
	 * 		calcola Z
	 * @throws ZScoreException 
	 */
	private void computeZScore() throws ZScoreException{
		M   = new double [dictionary.attributes.length];
		X   = new double [dictionary.attributes.length];
		EM2 = new double [dictionary.attributes.length];
		Z   = new double [dictionary.attributes.length];
		
		double [] Mtot   = new double [dictionary.attributes.length];
		double [] Xtot   = new double [dictionary.attributes.length];
		double [] EM2tot   = new double [dictionary.attributes.length];
		
		double [] tweetMoodArray;
		boolean tweetContained;
		
		
		for(int i = 0 ; i < Mtot.length ; i++){
			Mtot[i] = Xtot[i] = EM2tot[i] = 0;
		}
		
		tweetsInInterval = totalTweets = 0;
		
		for(SingleTweet tweet : tweets){
			//prendo l'array che rappresenta l'umore del tweet
			tweetMoodArray = dictionary.getTweetMood(tweet.text);
			
			if(tweetMoodArray == null) //ignoro i tweet non valutabili
				continue;
			
			totalTweets++; //aggiorno il numero di tweet totali
			
			tweetContained = tweet.containedInTimeInterval(timeInterval);
			
			if(tweetContained){ //se il tweet e' stato pubblicato nell'intervallo di tempo specificato
				tweetsInInterval++; //aggiorno il numero di tweet contenuti nell'intervallo di tempo
			}
			
			for(int i = 0 ; i < tweetMoodArray.length ; i++){
				Mtot[i]    += tweetMoodArray[i]; //aggiorno Mtot
				EM2tot[i]  += tweetMoodArray[i] * tweetMoodArray[i]; //aggiorno EX2tot
				
				if(tweetContained){ //se il tweet e' stato pubblicato nell'intervallo di tempo specificato
					Xtot[i] += tweetMoodArray[i]; //aggiorno Xtot
				}
			}
			
		}
		
		if(tweetsInInterval == 0)
			throw new ZScoreException("Nessun tweet nell'intervallo specificato");
		
		for(int i = 0 ; i < M.length ; i++){
			M[i]   = Mtot[i]   / totalTweets;
			X[i]   = Xtot[i]   / tweetsInInterval;
			EM2[i] = EM2tot[i] / totalTweets;
			
			Z[i] = (X[i] - M[i]) / Math.sqrt( EM2[i] - (M[i]*M[i]) ) ;
		}
		
	}
	
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.###");
		String ret = "";
		
		ret += "Numero tweet totali: " + tweets.size() + "\n";
		ret += "Intervallo specificato: " + timeInterval.toString() + "\n";
		ret += "Numero tweet nell'intervallo: " + tweetsInInterval + "\n";
		
		ret += "Attributi: ";
		for(int i = 0 ; i < dictionary.attributes.length ; i++){
			ret += dictionary.attributes[i];
			if(i == dictionary.attributes.length-1){
				ret += "\n\n";
			} else {
				ret += ", ";
			}
		}
		
		ret += "Medie attributi per singolo tweet (su tutti i tweet): [";
		for(int i = 0 ; i < M.length ; i++){
			ret += df.format(M[i]);
			if(i == M.length-1){
				ret += "]\n";
			} else {
				ret += ", ";
			}
		}
		
		ret += "Medie attributi per singolo tweet (solo sui tweet dell'intervallo): [";
		for(int i = 0 ; i < X.length ; i++){
			ret += df.format(X[i]);
			if(i == X.length-1){
				ret += "]\n";
			} else {
				ret += ", ";
			}
		}
		
		ret += "E(X^2) (su tutti i tweet): ";
		for(int i = 0 ; i < EM2.length ; i++){
			ret += df.format(EM2[i]);
			if(i == EM2.length-1){
				ret += "]\n";
			} else {
				ret += ", ";
			}
		}
		
		ret += "Z-Score per i tweet dell'intervallo: ";
		for(int i = 0 ; i < Z.length ; i++){
			ret += df.format(Z[i]);
			if(i == Z.length-1){
				ret += "]\n";
			} else {
				ret += ", ";
			}
		}
		
		
		return ret;
	}
	
	public double[] zscore(){
		return this.Z;
	}

}
