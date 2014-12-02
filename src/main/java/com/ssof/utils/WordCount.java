package com.ssof.utils;

/**
 * Un WordCount e' una coppia <Parola, Numero Occorrenze>.
 * Due WordCount vengono considerati uguali, ossia a.equals(b) == true,
 * se la loro parola e' uguale.
 *
 */
public class WordCount implements Comparable<WordCount>{
	public final String word;
	public int count;
	
	public WordCount(String word){
		this.word  = word;
		this.count = 0;
	}
	
	public WordCount(String word, int count) {
		this.word  = word;
		this.count = count;
	}

	public void incrementCount(){
		count++;
	}
	
	/**
	 * Considero due WordCount uguali se si riferiscono alla stessa parola.
	 */
	public boolean equals(Object x){
		if(x==null)
			return false;
		
		if(x.getClass() != getClass())
			return false;
		
		WordCount c = (WordCount) x;
		
		return c.word.equals(word);
	}

	public int compareTo(WordCount o) {
		return word.compareTo(o.word);
	}

	public String toString(){
		return "[" + word + ", " + count + "]";
	}
}
