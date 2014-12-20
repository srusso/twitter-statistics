package com.ssof.utils;

import com.ssof.exceptions.MultipleWordsException;
import com.ssof.twitter.SingleTweet;
import com.ssof.utils.comparators.WordCountComparatorCount;
import com.ssof.utils.comparators.WordCountComparatorWord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serve per estrarre le parole piu' usate data una lista di tweet, e salvarle su file.
 * @author Simone
 *
 */
public class ExtractWords {	
	/**
	 * Ritorna una lista contenente le parole piu' utilizzate nei tweet passati come argomento
	 * @param tweets Lista di tweet
	 * @param numOfWords Numero di parole da ritornare
	 * @return Lista di String
	 */
	public static List <String> getMostUsedWords(List <SingleTweet> tweets, int numOfWords){
		List <WordCount> wordList = new ArrayList<>();
		List <String> ret = new ArrayList<>();
		int wordsToReturn;
		int size = tweets.size();
		WordCountComparatorWord cw = new WordCountComparatorWord();
		WordCountComparatorCount cc = new WordCountComparatorCount(WordCountComparatorCount.ORDER_DESCENDING);

		for (SingleTweet tweet : tweets) { //per ogni tweet
			String[] words = StringUtils.getTweetWords(tweet.text);

			for (String word : words) { //per ogni parola di quel tweet
				if (word.length() < 3)
					continue;

				WordCount e = new WordCount(word);
				int index = Collections.binarySearch(wordList, e, cw);

				if (index < 0) { //se la parola non c'e' la aggiungo al posto giusto
					wordList.add(-index - 1, e);
				} else { //se invece gia c'e' aggiorno il suo contatore
					WordCount t = wordList.get(index);
					wordList.remove(index);
					t.count++;
					wordList.add(index, t);
				}
			}
		}
		
		
		if(numOfWords < wordList.size()){
			wordsToReturn = numOfWords;
		} else {
			wordsToReturn = wordList.size();
		}
		
		//ordino wordList per numero di occorrenze invece che in ordine alfabetico
		Collections.sort(wordList, cc);
		
		for(int i = 0 ; i < wordsToReturn ; i++){
			ret.add(wordList.get(i).word);
		}
		
		return ret;
	}
	
	/**
	 * Prende una lista di parole e le salva sul file specificato, una per riga.
	 * @param words
	 * @param filename
	 * @throws java.io.IOException Se ci sono errori durante l'apertura o la scrittura su file 
	 * @throws MultipleWordsException se una delle stringhe nella lista words contiene piu di una parola
	 */
	public static void saveWordlistToFile(List <String> words, String filename) throws IOException, MultipleWordsException{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);

		for (String word1 : words) {
			String[] word = word1.split(" ");
			if (word.length != 1) {
				out.close();
				throw new MultipleWordsException(word1);
			}
			out.print(word[0] + "\n");
		}
		
		out.close();
	}
	
}
