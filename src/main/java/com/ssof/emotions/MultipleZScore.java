package com.ssof.emotions;

import com.ssof.datatypes.TimePeriod;
import com.ssof.twitter.SingleTweet;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MultipleZScore {
	private Dictionary dictionary;
	private Collection<SingleTweet> tweets;

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

		final double[] m = new double[dictionary.attributes.length];
		final double[] EM2 = new double[dictionary.attributes.length];
		
		zscores = new ArrayList<>();
		
		double [] tweetMoodArray;

		long totalTweets = 0;
		
		for(SingleTweet tweet : tweets){
			//prendo l'array che rappresenta l'umore del tweet
			tweetMoodArray = dictionary.getTweetMood(tweet.text);
			
			if(tweetMoodArray == null) { //ignoro i tweet non valutabili
				continue;
			}
			
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
			for(int i = 0 ; i < m.length ; i++){
				m[i]   = Mtot[i]   / totalTweets;
				zs.X[i]   = zs.Xtot[i]   / zs.tweetsInInterval;
				EM2[i] = EM2tot[i] / totalTweets;

				zs.Z[i] = (zs.X[i] - m[i]) / Math.sqrt( EM2[i] - (m[i]* m[i]) ) ;
			}
		}
		
	}
	
	private SingleZScore getZScore(TimePeriod tp, List<SingleZScore> zscores) {
		for(SingleZScore zs : zscores){
			if(zs.timeInterval.equals(tp)) {
				return zs;
			}
		}
		
		return null;
	}

	private TimePeriod getDayTimePeriod(long millisSinceEpoch){
		DateTime startTime = new DateTime(millisSinceEpoch)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0);

		DateTime endTime = new DateTime(millisSinceEpoch)
			.withHourOfDay(23)
			.withMinuteOfHour(59)
			.withSecondOfMinute(59)
			.withMillisOfSecond(999);
		
		return new TimePeriod(startTime, endTime);	
	}

}
