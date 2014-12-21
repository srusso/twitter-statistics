package com.ssof.tweetsearch;

import com.ssof.twitter.SingleTweet;
import com.ssof.utils.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TweetSearch {
	private final List<SingleTweet> tweets;
	
	public TweetSearch(List <SingleTweet> tweets){
		this.tweets = tweets;
	}
	
	/**
	 * Ritorna la lista di tweet che rispettano i parametri passati come argomento.
	 * @param sp Parametri di ricerca
	 * @return Una lista di tweet
	 */
	public List <SingleTweet> getSearchResults(SearchParameters sp){
		List <SingleTweet> res = new ArrayList<>();

		for (SingleTweet tweet : tweets) {
			if (satParameters(tweet, sp))
				res.add(tweet);
		}
		
		return res;
	}
	
	private boolean satAllWords(String [] tweetWords, SearchParameters sp){
		//controllo se il tweet contiene tutte le parole di allWords, se ne incontro una che non contiene ritorno false
		Collection <String> allWords = sp.allWords;
		
		if(allWords == null)
			return true;
		
		for(String w1 : allWords){
			boolean c = false;
			for(String w2 : tweetWords){
				if(w1.equals(w2)){
					c = true;
					break;
				}
			}

			if(!c)
				return false;
		}
		
		
		return true;
	}

	private boolean satOneOrMore(String [] tweetWords, SearchParameters sp){
		Collection <String> oneOrMore = sp.oneOrMoreWords;
		
		if(oneOrMore == null)
			return true;
		
		//se il tweet contiene almeno una parola di oneOrMoreWords, ritorno true
		for(String word : tweetWords){
			if(oneOrMore.contains(word)){
				return true;
			}
		}

		return false;
	}
	
	private boolean satExactString(SingleTweet tweet, SearchParameters sp) {
		//se il tweet non contiene la stringa exactString, ritorno false
		return sp.exactString == null || tweet.text.contains(sp.exactString);
	}
	
	private boolean satAfterDate(DateTime tweetDate, SearchParameters sp) {
		return sp.afterDate == null || tweetDate.isAfter(sp.afterDate);
	}
	
	private boolean satBeforeDate(DateTime tweetDate, SearchParameters sp) {
		return sp.beforeDate == null || tweetDate.isBefore(sp.beforeDate);
	}
	
	private boolean satAuthor(SingleTweet tweet, SearchParameters sp) {
		return sp.author == null || sp.author.equalsIgnoreCase(tweet.user);
	}

	private boolean satSource(SingleTweet tweet, SearchParameters sp) {
		return sp.source == null || sp.source.length() == 0 || sp.source.equalsIgnoreCase(tweet.source);
	}

	private boolean satPlace(SingleTweet tweet, SearchParameters sp) {
		return sp.place == null || sp.place.length() == 0 || sp.place.equalsIgnoreCase(tweet.place);
	}

	/**
	 * Controlla se il tweet passato come argomento soddisfa i parametri specificati.
	 * @param tweet Il tweet
	 * @param sp I parametri
	 * @return True se il tweet rispetta i parametri, false altrimenti
	 */
	private boolean satParameters(SingleTweet tweet, SearchParameters sp) {
		String [] textwords = StringUtils.removeAccents(tweet.text).split(" "); //prendo il testo del tweet rimuovendo accenti, ad esempio "perch�" diventa "perche"
		
		//prendo la data del tweet
		DateTime tweetDate = new DateTime(tweet.millisSinceEpoch);

		return  satOneOrMore(textwords, sp) && satAllWords(textwords, sp) && satExactString(tweet, sp)
				&& satAfterDate(tweetDate, sp) && satBeforeDate(tweetDate, sp) && satAuthor(tweet, sp)
				&& satSource(tweet, sp) && satPlace(tweet, sp);
	}
}
