package com.ssof.datatypes;

import com.ssof.emotions.Dictionary;
import com.ssof.exceptions.NoSuchAttributeException;
import com.ssof.exceptions.NoSuchDayException;
import com.ssof.twitter.SingleTweet;
import com.ssof.utils.comparators.DateComparator;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Classe che contiene i dati necessari a disegnare il grafico
 * dell'andamento delle emozioni.
 * 
 * Per ogni giorno, contiene:
 * - Calendar rappresentante il giorno
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
	
	private final Map <Calendar, DayMoodData> dayData;
	
	private int evalued = 0;
	private int not_evalued = 0;
	
	public MoodData(List<SingleTweet> tweets, Dictionary dictionary) throws Exception{
		this.dictionary = dictionary;
		attributes      = dictionary.getAttributeArray();
		dayData 		= new HashMap<Calendar, DayMoodData>();
		
		long time1 = System.currentTimeMillis();
		
		Iterator <SingleTweet> iterator = tweets.iterator();
		
		while(iterator.hasNext()){
			SingleTweet t = iterator.next();
			Calendar d    = new GregorianCalendar();
			d.setTimeInMillis(t.millisSinceEpoch);
			d.set(Calendar.HOUR_OF_DAY, 0);
			d.set(Calendar.MINUTE, 0);
			d.set(Calendar.SECOND, 0);
			d.set(Calendar.MILLISECOND, 0);
			
			DayMoodData dmd = dayData.get(d);
			
			double [] mood = dictionary.getTweetMood(t.text);
			
			if(mood!=null){
				evalued++;
				if(dmd == null){
					dmd = new DayMoodData(dictionary);
					dmd.updateTotal(mood);
				
					dayData.put(d, dmd);
				}else {
					dmd.updateTotal(mood);
				}
			} else {
				not_evalued++;
			}
			
		}
		
		Iterator <Calendar> i = dayData.keySet().iterator();
		
		while(i.hasNext()){
			DayMoodData data = dayData.get(i.next());
			
			data.setMean();
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
	public double getDayMean(Calendar day, String attribute) throws NoSuchAttributeException, NoSuchDayException{
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
	public Set <Calendar> getValidDays(){
		return dayData.keySet();
	}
	
	public Calendar getFirstDay(){
		return Collections.min(dayData.keySet(), new DateComparator());
	}
	
	public Calendar getLasttDay(){
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
