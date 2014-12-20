package com.ssof.utils;

import com.ssof.twitter.SingleTweet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TextAnalizer {
	private static TextAnalizer ta = null;
	public static String resourceDirectory = System.getProperty("user.dir") + File.separator + "resources";
	
	
	/**
	 * Se in una frase ci sono n parole di cui i italiane, considero tale frase scritta in italiano
	 * se e solo se i/n > MIN_PERC
	 */
	private final double MIN_PERC = 0.5;
	
	private ArrayList <String> wordlist;
	private File wordlistFile;
	
	private TextAnalizer(File wordlistFile) throws FileNotFoundException{
		wordlist = null;
		setNewWordlist(wordlistFile);
	}
	
	/**
	 * Ritorna l'istanza del TextAnalizer, con la wordlist del file wordlistfile caricata in memoria.
	 * Crea una nuova istanza quando viene chiamata per la prima volta o quando
	 * viene specificato un file diverso da quello attualmente caricato in memoria.
	 * Altrimenti ritorna semplicemente un'istanza gia creata.
	 * 
	 * @param wordlistFile File contenente la wordlist desiderata
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	public static TextAnalizer getInstance(File wordlistFile) throws FileNotFoundException{
		if(ta == null){
			ta = new TextAnalizer(wordlistFile);
		} else if(ta.wordlistFile != wordlistFile){
			ta.wordlist.clear();
			
			ta = new TextAnalizer(wordlistFile);
		}
		
		return ta;
	}
	
	/**
	 * Ritorna l'istanza del TextAnalizer.
	 * Se ritorna null, vuol dire che non e' stato ancora iniziato il TextAnalizer,
	 * e quindi bisogna chiamare getInstance(File).
	 * @return
	 */
	public static TextAnalizer getInstance(){
		return ta;
	}
	
	
	/**
	 * Data una lista di tweet, restituisce un numero double tra 0 e 100
	 * rappresentante la percentuale di tweet individuati come italiani.
	 * @param tweets La lista dei tweet da analizzare
	 * @return Un double tra 0 e 100
	 */
	public double percItalian(List <SingleTweet> tweets){
		int size = tweets.size();
		double ita = 0;
		double tot = tweets.size();

		for (SingleTweet tweet : tweets) {
			if (isTextItalian(tweet.text)) {
				ita++;
			}
		}
		
		if(size == 0) {
			return 0;
		}
		
		return 100*(ita/tot);
	}
	
	/**
	 * Ritorna true se riconosce il testo passato come parametro come scritto in Italiano.
	 * Ritorna false altrimenti.
	 * Se in una frase ci sono n parole di cui i italiane, considera tale frase scritta in italiano
	 * se e solo se i/n > MIN_PERC
	 * 
	 * @param text
	 * @return
	 */
	public boolean isTextItalian(String text) {
		String[] words = StringUtils.getTweetWords(text); //prendo le parole del tweet
		double tot = words.length, ita = 0;

		for (String w : words) {
			if (isItalian(w)) {
				ita += 1;
			}
		}

		return tot != 0 && (ita / tot) > MIN_PERC;
	}
	
	/**
	 * Ritorna true se la parola passata come parametro e' contenuta nella wordlist.
	 * @param w
	 * @return
	 */
	public boolean isItalian(String word) {
		//Se la ricerca binaria della parola sulla wordlist va a buon fine,
		//binarySearch() ritorna un valore >=0
		return Collections.binarySearch(wordlist, word)>=0;
	}

	/**
	 * Svuota la wordlist caricata in memoria e carica quella che si trova
	 * nel file newfile.
	 * @param newfile File contenente la wordlist da caricare in memoria
	 * @throws java.io.FileNotFoundException se il file newfile non esiste
	 */
	private void setNewWordlist(File newfile) throws FileNotFoundException{
		int bytesToRead;
		final int max_bytes = 1000;
		
		wordlistFile = newfile;
		
		if(wordlist != null) {
			wordlist.clear();
		}
		
		wordlist = new ArrayList<>();
		
		FileChannel roChannel = new RandomAccessFile(wordlistFile, "r").getChannel();
		
		try {
			MappedByteBuffer readbuffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int)roChannel.size());
			byte [] array = new byte [max_bytes];
			boolean trunc = false;
			String string;
			int i;
			
			while(readbuffer.hasRemaining()) {
				i=0;				
				
				//determino quanti byte leggere, assicurandomi che tale numero non sia mai maggiore
				//dei byte rimanenti nel buffer
				bytesToRead = readbuffer.remaining();
				if(bytesToRead < max_bytes){
					array = new byte [bytesToRead];
				}
				
				readbuffer.get(array);
				
				string = new String(array);
				String [] words = string.split("\n");
				
				if(trunc){
					trunc = !string.startsWith("\n");
					String newlast = wordlist.get(wordlist.size()-1);
					wordlist.remove(wordlist.size()-1);
					newlast += words[0];
					if(newlast.length() > 0) wordlist.add(newlast);
					i++;
				}
				
				
				for( ; i < words.length ; i++ ){ //aggiungo alla lista tutte le parole appena lette compresa l'ultima, che potrebbe essere troncata
					if(words[i].length() > 0) wordlist.add(words[i]);
				}				
				
				trunc = !string.endsWith("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Collections.sort(wordlist); //in realta non dovrebbe servire dato che la lista dovrebbe gia essere ordinata alfabeticamente
	}

	/**
	 * Prende i tweet passati come parametro e ci toglie
	 * i tweet non italiani.
	 * @param tweets
	 * @return I tweet, senza quelli non italiani
	 */
	public List<SingleTweet> filterItalianTweets(List<SingleTweet> tweets) {
		List <SingleTweet> ret = new LinkedList<>();
		
		for(SingleTweet t : tweets){
			if(isTextItalian(t.text)) {
				ret.add(t);
			}
		}
		
		return ret;
	}

}
