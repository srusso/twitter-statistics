package com.ssof.datatypes;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import ts.twitter.SingleTweet;
import ts.utils.comparators.SingleTweetComparator;

/**
 * Classe che contiene una lista di tweet,
 * tutti contenenti una certa parola.
 * @author Simone
 *
 */
public class WordTimelineTweets{
	/**
	 * La parola contenuta in tutti i tweet
	 */
	public final String word;
	
	/**
	 * La lista di tweet.
	 */
	public final TreeSet <SingleTweet> tweets;
	
	/**
	 * 
	 * @param word La parola contenuta da tutti i tweet della lista passata come secondo argomento
	 * @param tweets La lista di tweet
	 */
	public WordTimelineTweets(String word, List <SingleTweet> tweets){
		this.word   = word;
		this.tweets = new TreeSet<SingleTweet>(new SingleTweetComparator(SingleTweetComparator.ORDER_BY_DATE, SingleTweetComparator.ASCENDING));
		
		Iterator <SingleTweet> i = tweets.iterator();
		while(i.hasNext()){
			this.tweets.add(i.next());
		}
	}
	
	/**
	 * Ritorna il tweet piu' vecchio di questa timeline.
	 * @return Un'oggetto SingleTweet
	 */
	public SingleTweet getOldestTweet(){
		return tweets.first();
	}
	
	/**
	 * Ritorna il tweet piu' recente di questa timeline.
	 * @return Un'oggetto SingleTweet
	 */
	public SingleTweet getNewestTweet(){
		return tweets.last();
	}
}
