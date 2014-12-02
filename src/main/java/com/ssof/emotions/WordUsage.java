package com.ssof.emotions;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import ts.twitter.SingleTweet;
import ts.utils.WordCount;

public class WordUsage {
	/**
	 * Due parole di un tweet sono considerate "vicine" se distano meno di NEAR_DIST tra loro.
	 */
	private final int NEAR_DIST = 2;
	
	public final HashMap <String, TreeSet<WordCount>> map;
	
	
	/**
	 * Data una lista di tweet, costruisce un'array contenente, per ogni
	 * parola presente nei tweet, la lista di parole utilizzate insieme ad essa.
	 * Tali liste sono inoltre ordinate per numero di occorrenze.
	 * @param tweets
	 */
	public WordUsage(Dictionary dictionary, List <SingleTweet> tweets){
		map = new HashMap<String, TreeSet<WordCount>>();
		getWordUsage(dictionary, tweets);
	}
	
	/**
	 * Costruisce la mappa [String, TreeSet[WordCount]].
	 * 
	 * NOTA BENE: I tweet ricevuti come parametro devono essere solo italiani, senza accenti e senza articoli.
	 */
	private void getWordUsage(Dictionary dictionary, List <SingleTweet> tweets){
		HashMap <String, HashMap<String, Integer>> tempMap = new HashMap<String, HashMap<String, Integer>>();
		
		//per ogni tweet
		for(SingleTweet tweet : tweets){
			String [] tweetWords = tweet.text.split(" "); //prendo le parole del tweet. ATTENZIONE: i tweet devono essere solo italiani, senza accenti, senza articoli, senza caratteri che non siano lettere.
														  //in pratica devono essere stati gia passati attraverso la StringUtils.getTweetWordsNoArticlesNoAccents()

			for(int i = 0 ; i < tweetWords.length ; i++){ //per ogni parola del tweet...**
				if(!dictionary.containsWord(tweetWords[i])) continue; //**..che sia anche parola del lessico..**
				
				HashMap<String, Integer> iWordCount = tempMap.get(tweetWords[i]);  //prendo la lista di parole associate a tweetWords[i]
				if(iWordCount == null){ //se non c'e' perche' e' la prima volta che incontro questa parola, la creo
					iWordCount = new HashMap<String, Integer>();
					tempMap.put(tweetWords[i], iWordCount); //aggiungo tale lista alla mappa
				}
				
				int end = (i+NEAR_DIST+1>=tweetWords.length)?tweetWords.length:(i+NEAR_DIST+1);

				for(int j = (i-NEAR_DIST<0)?0:(i-NEAR_DIST) ; j < end ; j++){ //**...considero le parole vicine
					if(i==j) continue;
					//tweetWords[i] = parola che sto considerando
					//tweetWords[j] = una delle parole che gli sta vicina

					Integer k = iWordCount.get(tweetWords[j]); //k = numero di co-occorrenze di j per i
					
					//aggiorno tale valore
					if(k!=null)
						iWordCount.put(tweetWords[j], k+1);
					else iWordCount.put(tweetWords[j], 1);
				}
				
			}
			
		}
		
		//converto da HashMap <String, HashMap<String, Integer>> a HashMap<String, TreeSet<WordCount>>
		Set <String> keySet = tempMap.keySet();
		for(String key : keySet){				
			map.put(key, getTreeSetFromHashMap(tempMap.get(key)));
		}
		
	}

	private TreeSet<WordCount> getTreeSetFromHashMap(HashMap<String, Integer> hm) {
		TreeSet <WordCount> treeSet = new TreeSet<WordCount>();
		
		Set <String> keySet = hm.keySet();
		for(String key : keySet){
			treeSet.add(new WordCount(key, hm.get(key)));
		}
		
		return treeSet;
	}

}
