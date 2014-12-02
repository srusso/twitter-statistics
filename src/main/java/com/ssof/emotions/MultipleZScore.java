package com.ssof.emotions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import ts.datatypes.TimePeriod;
import ts.exceptions.ZScoreException;
import ts.twitter.SingleTweet;

public class MultipleZScore {
	private Dictionary dictionary;
	private Collection <SingleTweet> tweets;

	private double [] M;
	private double [] EM2;

	private long totalTweets;
	
	public List <SingleZScore> zscores;
	
	public MultipleZScore(Dictionary d, Collection <SingleTweet> tweets){
		this.dictionary    = d;
		this.tweets        = tweets;
		
		computeZScores();
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
	private void computeZScores(){
		double [] Mtot   = new double [dictionary.attributes.length];
		double [] EM2tot   = new double [dictionary.attributes.length];
		
		M   = new double [dictionary.attributes.length];
		EM2 = new double [dictionary.attributes.length];
		
		zscores = new ArrayList<SingleZScore>();
		
		double [] tweetMoodArray;
		
		totalTweets = 0;
		
		for(SingleTweet tweet : tweets){
			//prendo l'array che rappresenta l'umore del tweet
			tweetMoodArray = dictionary.getTweetMood(tweet.text);
			
			if(tweetMoodArray == null) //ignoro i tweet non valutabili
				continue;
			
			TimePeriod tp = getDayTimePeriod(tweet.millisSinceEpoch);
			SingleZScore zs = getZScore(tp, zscores);
			
			if(zs == null){
				zs = new SingleZScore(dictionary, tp);
				zscores.add(zs);
			}
			
			totalTweets++; //aggiorno il numero di tweet totali
			
			zs.tweetsInInterval++; //aggiorno il numero di tweet contenuti nell'intervallo di tempo [cosi poi posso fare la media di Xtot]
			
			for(int i = 0 ; i < tweetMoodArray.length ; i++){
				Mtot[i]    += tweetMoodArray[i]; //aggiorno Mtot
				EM2tot[i]  += tweetMoodArray[i] * tweetMoodArray[i]; //aggiorno EX2tot
				zs.Xtot[i] += tweetMoodArray[i]; //aggiorno Xtot
			}
			
		}
		
		for(SingleZScore zs : zscores){
			for(int i = 0 ; i < M.length ; i++){
				M[i]   = Mtot[i]   / totalTweets;
				zs.X[i]   = zs.Xtot[i]   / zs.tweetsInInterval;
				EM2[i] = EM2tot[i] / totalTweets;

				zs.Z[i] = (zs.X[i] - M[i]) / Math.sqrt( EM2[i] - (M[i]*M[i]) ) ;
			}
		}
		
	}
	
	private SingleZScore getZScore(TimePeriod tp, List<SingleZScore> zscores) {
		for(SingleZScore zs : zscores){
			if(zs.timeInterval.equals(tp))
				return zs;
		}
		
		return null;
	}

	private TimePeriod getDayTimePeriod(long millisSinceEpoch){
		Calendar startTime, endTime;
		
		startTime = new GregorianCalendar();
		endTime   = new GregorianCalendar();
	
		startTime.setTimeInMillis(millisSinceEpoch);
		endTime.setTimeInMillis(millisSinceEpoch);
		
		startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
		startTime.set(GregorianCalendar.MINUTE, 0);
		startTime.set(GregorianCalendar.SECOND, 0);
		startTime.set(GregorianCalendar.MILLISECOND, 0);
		
		endTime.set(GregorianCalendar.HOUR_OF_DAY, 23);
		endTime.set(GregorianCalendar.MINUTE, 59);
		endTime.set(GregorianCalendar.SECOND, 59);
		endTime.set(GregorianCalendar.MILLISECOND, 999);
		
		
		return new TimePeriod(startTime, endTime);	
	}

}
