package com.ssof.tweetsearch;

import com.ssof.twitter.SingleTweet;
import com.ssof.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
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
	
	private boolean satExactString(SingleTweet tweet, SearchParameters sp){
		//se il tweet non contiene la stringa exactString, ritorno false
		if(sp.exactString == null)
			return true;
		
		return tweet.text.contains(sp.exactString);
	}
	
	private boolean satAfterDate(GregorianCalendar tweetDate, SearchParameters sp){
		if(sp.afterDate == null)
			return true;
		
		return tweetDate.after(sp.afterDate);
	}
	
	private boolean satBeforeDate(GregorianCalendar tweetDate, SearchParameters sp){
		if(sp.beforeDate == null)
			return true;
		
		return tweetDate.before(sp.beforeDate);
		
	}
	
	private boolean satAuthor(SingleTweet tweet, SearchParameters sp){
		if(sp.author == null)
			return true;
		
		return sp.author.equalsIgnoreCase(tweet.user);
	}

	private boolean satSource(SingleTweet tweet, SearchParameters sp){
		if(sp.source == null  || sp.source.length() == 0)
			return true;
		
		return sp.source.equalsIgnoreCase(tweet.source);
	}

	private boolean satPlace(SingleTweet tweet, SearchParameters sp){
		if(sp.place == null  || sp.place.length() == 0)
			return true;
		
		return sp.place.equalsIgnoreCase(tweet.place);
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
		GregorianCalendar tweetDate = new GregorianCalendar();
		tweetDate.setTimeInMillis(tweet.millisSinceEpoch);

		return  satOneOrMore(textwords, sp) && satAllWords(textwords, sp) && satExactString(tweet, sp)
				&& satAfterDate(tweetDate, sp) && satBeforeDate(tweetDate, sp) && satAuthor(tweet, sp)
				&& satSource(tweet, sp) && satPlace(tweet, sp);
	}
}
