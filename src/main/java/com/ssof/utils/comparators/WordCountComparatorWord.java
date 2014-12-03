package com.ssof.utils.comparators;

import com.ssof.utils.WordCount;

import java.util.Comparator;

public class WordCountComparatorWord implements Comparator<WordCount> {

	public int compare(WordCount a, WordCount b) {
		return a.word.compareTo(b.word);
	}
	
}
