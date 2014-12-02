package com.ssof.utils.comparators;

import java.util.Comparator;

import ts.utils.WordCount;

public class WordCountComparatorWord implements Comparator <WordCount>{

	public int compare(WordCount a, WordCount b) {
		return a.word.compareTo(b.word);
	}
	
}
