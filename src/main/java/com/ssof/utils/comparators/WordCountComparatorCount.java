package com.ssof.utils.comparators;

import java.util.Comparator;

import ts.utils.WordCount;

public class WordCountComparatorCount implements Comparator <WordCount>{
	public static final int ORDER_ASCENDING = 1;
	public static final int ORDER_DESCENDING = -1;
	
	private final int order;
	
	/**
	 * Ordina in modo discendente.
	 */
	public WordCountComparatorCount(){
		this.order = ORDER_DESCENDING;
	}
	
	public WordCountComparatorCount(int order){
		this.order = order;
	}

	public int compare(WordCount a, WordCount b) {
		if(a.count < b.count){
			return -order;
		} else if(a.count == b.count){
			return 0;
		} else return order;
	}

}