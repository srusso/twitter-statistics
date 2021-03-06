package com.ssof.datatypes;

import com.ssof.emotions.Dictionary;
import com.ssof.exceptions.NoSuchAttributeException;
import com.ssof.exceptions.NoSuchDayException;
import com.ssof.twitter.SingleTweet;
import com.ssof.utils.comparators.DateComparator;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Classe che contiene i dati necessari a disegnare il grafico
 * dell'andamento delle emozioni.
 * 
 * Per ogni giorno, contiene:
 * - DateTime rappresentante il giorno
 * - Media, per singolo tweet, di ogni attributo del lessico
 * @author Simone
 *
 */
public class MoodData {	
	private Dictionary dictionary;	
	
	/**
	 * Gli attributi del dizionario, tenuti qui per comodita'.
	 */
	private final String [] attributes;
	
	private final Map <DateTime, DayMoodData> dayData;
	
	private int evalued = 0;
	private int not_evalued = 0;
	
	public MoodData(List<SingleTweet> tweets, Dictionary dictionary) throws Exception{
		this.dictionary = dictionary;
		attributes      = dictionary.getAttributeArray();
		dayData 		= new HashMap<>();
		
		long time1 = System.currentTimeMillis();

		for (SingleTweet t : tweets) {
			DateTime d = new DateTime(t.millisSinceEpoch)
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfDay(0);

			DayMoodData dmd = dayData.get(d);

			double[] mood = dictionary.getTweetMood(t.text);

			if (mood != null) {
				evalued++;
				if (dmd == null) {
					dmd = new DayMoodData(dictionary);
					dmd.updateTotal(mood);

					dayData.put(d, dmd);
				} else {
					dmd.updateTotal(mood);
				}
			} else {
				not_evalued++;
			}

		}

		for (DateTime date : dayData.keySet()) {
			dayData.get(date).setMean();
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("Tempo MoodData con " + tweets.size() + " tweets: " + (time2-time1) + " ms.");
		
	}

	/**
	 * Ritorna la media per tweet dell'attributo attribute, nel giorno specificato.
	 * Per avere gli attributi validi: getValidAttributes()
	 * Per avere i giorni validi: getValidDays()
	 * @param day
	 * @param attribute
	 * @return
	 * @throws NoSuchDayException Se non ci sono dati per il giorno specificato
	 * @throws NoSuchAttributeException Se l'attributo specificato non e' presente nei lessico
	 */
	public double getDayMean(DateTime day, String attribute) throws NoSuchAttributeException, NoSuchDayException{
		int a;
		
		for(a = 0 ; a < attributes.length ; a++){
			if(attributes[a].equals(attribute)){
				break;
			}
		}
		
		if(a == attributes.length)
			throw new NoSuchAttributeException(attribute);
		
		
		DayMoodData dmdata = dayData.get(day);
		
		if(dmdata != null)
			return dmdata.mean[a];			
		else
			throw new NoSuchDayException(day);
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
	public Set<DateTime> getValidDays(){
		return dayData.keySet();
	}
	
	public DateTime getFirstDay(){
		return Collections.min(dayData.keySet(), new DateComparator());
	}
	
	public DateTime getLasttDay(){
		return Collections.max(dayData.keySet(), new DateComparator());
	}

	public int getAttributePosition(String attribute) {
		for(int i = 0 ; i < attributes.length ; i++)
			if(attributes[i].equals(attribute))
				return i;
		
		throw new RuntimeException();
	}
	
	/**
	 * Ritorna la percentuale di messaggi che e' stato possibile analizzare con il dizionario fornito.
	 * @return
	 */
	public double getEvaluedPercentage(){
		double ev = evalued;
		return (100 * ev) / (evalued + not_evalued);
	}
}
