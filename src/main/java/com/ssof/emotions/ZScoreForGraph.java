package com.ssof.emotions;

import com.ssof.datatypes.TimePeriod;
import com.ssof.exceptions.NoSuchAttributeException;
import com.ssof.exceptions.NoSuchDayException;
import com.ssof.twitter.SingleTweet;
import com.ssof.utils.comparators.DateComparator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ZScoreForGraph {
	private Dictionary dictionary;
	private Collection<SingleTweet> tweets;

	private double [] M;
	private double [] EM2;

	private long totalTweets;
	
	public List <SingleZScore> zscores;
	
	private final String [] attributes;
	
	public ZScoreForGraph(Dictionary d, Collection <SingleTweet> tweets){
		this.dictionary = d;
		this.tweets     = tweets;
		this.attributes = d.attributes;
		
		computeZScores();
		
		Collections.sort(zscores, new Comparator<SingleZScore>(){

			public int compare(SingleZScore a, SingleZScore b) {
				long av = a.timeInterval.getStart();
				long bv = b.timeInterval.getStart();
				
				if(av < bv)
					return -1;
				else if (av == bv)
					return 0;
				else return 1;
			}
			
		});
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
	 */
	private void computeZScores(){
		double [] Mtot   = new double [dictionary.attributes.length];
		double [] EM2tot   = new double [dictionary.attributes.length];
		
		M   = new double [dictionary.attributes.length];
		EM2 = new double [dictionary.attributes.length];
		
		zscores = new ArrayList<>();
		
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
	
	public double getDayValue(DateTime day, String attribute) throws NoSuchAttributeException, NoSuchDayException {
		int a;
		
		for(a = 0 ; a < attributes.length ; a++){
			if(attributes[a].equals(attribute)){
				break;
			}
		}
		
		if(a == attributes.length)
			throw new NoSuchAttributeException(attribute);
		
		SingleZScore found = null;
		for(SingleZScore zs : zscores){
			DateTime c = new DateTime(zs.timeInterval.getStart());
			if(c.getDayOfMonth() == day.getDayOfMonth() &&
				c.getMonthOfYear() == day.getMonthOfYear() &&
				c.getYear() == day.getYear()){
				found = zs;
				break;
			}
		}
		
		if(found != null)
			return found.Z[a];			
		else
			throw new NoSuchDayException(day);
	}
	
	private SingleZScore getZScore(TimePeriod tp, List<SingleZScore> zscores) {
		for(SingleZScore zs : zscores){
			if(zs.timeInterval.equals(tp))
				return zs;
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

	/**
	 * Ritorna gli attributi disponibili, cioe' le emozioni
	 * presenti nel lessico. Sono i valori legali da passare
	 * al metodo getDayMean() come parametro attribute.
	 * @return
	 */
	public Set <String> getValidAttributes(){
		return dictionary.getAttributes();
	}
	
	/**
	 * Ritorna i giorni sui quali sono disponibili i dati.
	 * Sono i valori legali da passare
	 * al metodo getDayMean() come parametro day.
	 * @return
	 */
	public Set <DateTime> getValidDays(){
		Set <DateTime> days = new TreeSet<>();

		for(SingleZScore zs : zscores){
			days.add(new DateTime(zs.timeInterval.getStart()));
		}

		return days;
	}
	
	public DateTime getFirstDay(){
		return Collections.min(getValidDays(), new DateComparator());
	}
	
	public DateTime getLasttDay(){
		return Collections.max(getValidDays(), new DateComparator());
	}

	public int getAttributePosition(String attribute) {
		for(int i = 0 ; i < attributes.length ; i++)
			if(attributes[i].equals(attribute))
				return i;
		
		throw new RuntimeException();
	}

}
