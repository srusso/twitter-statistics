package com.ssof.utils.comparators;


import com.ssof.twitter.SingleTweet;

import java.util.Comparator;

/**
 * Comparatore per poter ordinare una lista di SingleTweet secondo il campo desiderato.
 * @author Simone
 *
 */
public class SingleTweetComparator implements Comparator<SingleTweet> {
	/**
	 * Ordina i tweet alfabeticamente per autore.
	 */
	public static final int ORDER_BY_USERNAME = 1;
	
	/**
	 * Ordina i tweet per data di creazione.
	 */
	public static final int ORDER_BY_DATE = 2;
	
	/**
	 * Ordina i tweet alfabeticamente per localita.
	 */
	public static final int ORDER_BY_PLACE = 3;
	
	/**
	 * Ordina i tweet alfabeticamente per programma utilizzato.
	 */
	public static final int ORDER_BY_SOURCE = 4;
	
	/**
	 * Per ordinare i tweet in ordine ascendente.
	 */
	public static final int ASCENDING = 1;
	
	/**
	 * Per ordinare i tweet in ordine discendente.
	 */
	public static final int DESCENDING = -1;
	
	
	private final int field;
	private final int order;
	
	/**
	 * Crea un nuovo comparatore di SingleTweet utilizzando i parametri specificati
	 * @param fieldToCompare Uno tra ORDER_BY_USERNAME, ORDER_BY_DATE, ORDER_BY_PLACE, ORDER_BY_SOURCE
	 * @param order Uno tra ASCENDING e DESCENDING
	 */
	public SingleTweetComparator(int fieldToCompare, int order) throws RuntimeException{
		if(fieldToCompare != ORDER_BY_USERNAME && fieldToCompare != ORDER_BY_DATE && fieldToCompare != ORDER_BY_PLACE && fieldToCompare != ORDER_BY_SOURCE)
			throw new RuntimeException("Errore: il primo argomento del costruttore di SingleTweetComparator deve essere uno tra quelli definiti staticamente in tale classe.");
		
		if(order != ASCENDING && order != DESCENDING)
			throw new RuntimeException("Errore: il secondo argomento del costruttore di SingleTweetComparator deve essere uno tra quelli definiti staticamente in tale classe.");
		
		this.field = fieldToCompare;
		this.order = order;
	}

	public int compare(SingleTweet a, SingleTweet b) {
		
		if(field == ORDER_BY_DATE){
			
			int ret;
			
			if(a.millisSinceEpoch < b.millisSinceEpoch){
				ret = -1;
			} else if(a.millisSinceEpoch == b.millisSinceEpoch){
				ret = 0;
			} else ret = 1;
			
			return order * ret;
			
		} else if(field == ORDER_BY_USERNAME) {
			
			return order * a.user.compareTo(b.user);

		} else if(field == ORDER_BY_PLACE) {
			
			return order * a.place.compareTo(b.place);
			
		} else { //ORDER_BY_SOURCE
			
			return order * a.source.compareTo(b.source);
			
		}
		
	}
}
