package com.ssof.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class StringUtils {
	private static final String URL_START = "http";
	private static final String TAG_START = "#";
	private static final String AT_START = "@";
	
	static Set<String> articles = new HashSet<>();
	
	static {
		articles.add("del");
		articles.add("degli");
		articles.add("dello");
		articles.add("della");
		articles.add("delle");
		articles.add("che");
		articles.add("tra");
		articles.add("fra");
		articles.add("il");
		articles.add("lo");
		articles.add("la");
		articles.add("gli");
		articles.add("le");
	}
	
	/**
	 * Ritorna una stringa uguale a quella passata come parametro,
	 * trasformando le lettere accentate in lettere non accentate.
	 * @param str La stringa dalla quale eliminare gli accenti
	 * @return La stringa senza accenti
	 */
	public static String removeAccents(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll(""); 
	}
	
	/**
	 * Ritorna un array contenente le parole del tweet, sia italiane che non, compresi gli articoli.
	 * Elimina solo url e hashtag.
	 */
	public static String [] getTweetWords(String tweetText){
		String [] words = removeAccents(tweetText).split("[^\\w[\\d]&&[^@]]+");
		ArrayList <String> list = new ArrayList <>();

		for (final String word : words) {
			if (!isUrlOrTag(word))
				list.add(word);
		}
		
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * Ritorna un array contenente le parole del tweet, sia italiane che non,
	 * eliminando articoli, parole pi� corte di due caratteri, url e hashtag.
	 * Toglie gli accenti dalle parole.
	 */
	public static String [] getTweetWordsNoArticlesNoAccents(String tweetText){
		String [] words = removeAccents(tweetText).split("[^\\w[\\d]&&[^@]]+");
		ArrayList <String> list = new ArrayList <>();

		for (final String word : words) {
			if (!isArticle(word) && !isUrlOrTag(word) && word.length() > 2)
				list.add(word);
		}
		
		return list.toArray(new String[list.size()]);
	}

	private static boolean isArticle(String word) {
		return articles.contains(word);
	}

	private static boolean isUrlOrTag(String string) {
		return string.startsWith(AT_START) || string.startsWith(TAG_START) || string.startsWith(URL_START);
	}
}
