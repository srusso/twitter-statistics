package com.ssof.emotions;


import com.ssof.exceptions.DictionaryException;
import com.ssof.twitter.SingleTweet;
import com.ssof.utils.ArrayMath;
import com.ssof.utils.StringUtils;
import com.ssof.utils.WordCount;
import com.ssof.utils.comparators.WordCountComparatorCount;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe che contiene informazioni sulle parole piu utilizzate e sulla
 * loro classificazione secondo diversi attributi.
 *
 */
public class Dictionary {
	public final String [] attributes;
	final String [] words;
	final int [][] attributeValues;
	/**
	 * Contiene le stringhe dell'array words.
	 * Serve per fare la contains() in modo piu' veloce.
	 */
	private final HashSet <String> hashWords;
	
	/**
	 * Contiene le stringhe dell'array attributes.
	 * Serve per fare la contains() in modo piu' veloce.
	 */
	private final Set <String> hashAttributes;
	
	public Dictionary(String [] attributes, String [] words, List <List <Integer>> attributeValues) throws DictionaryException{
		if(attributeValues.size() != words.length) {
			throw new DictionaryException("Il numero di righe di attributeValues deve essere uguale al numero di parole");
		}
		
		for(int i = 0 ; i < attributeValues.size() ; i ++){
			if(attributeValues.get(i).size() != attributes.length) {
				throw new DictionaryException(
					"La riga n. " + (i) + " dell'array attributeValues ha " + attributeValues.get(i).size() + " colonne, mentre ogni riga ne dovrebbe avere " + attributes.length +
					", cioe' pari al numero di attributi.");
			}
		}
		
		this.attributes = attributes;
		this.words = words;
		
		this.attributeValues = new int [attributeValues.size()][attributeValues.get(0).size()];
		
		for(int i = 0 ; i < attributeValues.size() ; i++){
			List <Integer> row = attributeValues.get(i);
			for(int j = 0 ; j < row.size() ; j++){
				this.attributeValues[i][j] = row.get(j);
			}
		}
		
		hashAttributes = new TreeSet<>();
		Collections.addAll(hashAttributes, attributes);
		
		hashWords = new HashSet<>();
		Collections.addAll(hashWords, words);
	}
	
	public Dictionary(List <String> attributes, List <String> words, List <List <Integer>> attributeValues) throws DictionaryException{
		this(attributes.toArray(new String[attributes.size()]), words.toArray(new String[words.size()]), attributeValues);
	}
	
	public Dictionary(List <String> attributes, String [] words, List <List <Integer>> attributeValues) throws DictionaryException{
		this(attributes.toArray(new String[attributes.size()]), words, attributeValues);
	}
	
	public Dictionary(String [] attributes, List <String> words, List <List <Integer>> attributeValues) throws DictionaryException{
		this(attributes, words.toArray(new String[words.size()]), attributeValues);
	}
	
	public Dictionary(Dictionary dictionary) throws DictionaryException{
		if(dictionary.attributeValues.length != dictionary.words.length)
			throw new DictionaryException("Il numero di righe di attributeValues deve essere uguale al numero di parole");
		
		for(int i = 0 ; i < dictionary.attributeValues.length ; i ++){
			if(dictionary.attributeValues[i].length != dictionary.attributes.length)
				throw new DictionaryException("La riga n. " + (i) + " dell'array attributeValues ha " + dictionary.attributeValues[i].length + " colonne, mentre ogni riga ne dovrebbe avere "
												+ dictionary.attributes.length + ", cioe' pari al numero di attributi.");
		}
		
		this.attributes = dictionary.attributes;
		this.words = dictionary.words;
		
		this.attributeValues = new int [dictionary.attributeValues.length][dictionary.attributeValues[0].length];

		for (int[] row : attributeValues) {
			System.arraycopy(row, 0, row, 0, row.length);
		}
		
		hashAttributes = new TreeSet<>();
		Collections.addAll(hashAttributes, attributes);
		
		hashWords = new HashSet<>();
		Collections.addAll(hashWords, words);
	}

	/**
	 * Ritorna il numero di termini in questo lessico.
	 * @return
	 */
	public int size(){
		return words.length;
	}
	
	public String[] getAttributeArray(){
		return attributes;
	}
	
	public Set <String> getAttributes(){
		return hashAttributes;
	}
	
	/**
	 * Ritorna true se questo lessico contiene la parola specificata.
	 * Ritorna false altrimenti.
	 * @param word
	 * @return
	 */
	public boolean containsWord(String word){
		return hashWords.contains(word);
	}
	
	public String toString(){
		String ret;
		ret = "Attributi: ";
		
		for(int i = 0 ; i < this.attributeValues[0].length ; i++){
			ret += (attributes[i]+" ");
		}
		ret += "\n";
		for(int i = 0 ; i < this.attributeValues.length ; i++){
			ret += ("Parola \'" +  words[i] + "\' con attributi: ");
			for(int j = 0 ; j < this.attributeValues[i].length ; j++){
				ret += (this.attributeValues[i][j] + " ");
			}
			ret += "\n";
		}
		
		return ret;
	}


	public Dictionary expand(ExpansionParameters ep) throws DictionaryException{
		WordCountComparatorCount cc = new WordCountComparatorCount(WordCountComparatorCount.ORDER_DESCENDING);
		
		//ora ho, per ogni parola del lessico, la lista di tutte le parole utilizzate con essa
		//ora ordino tali liste per numero di utilizzi e le taglio
		
		HashMap<String, TreeSet<WordCount>> map = ep.wordUsage.map;
		HashMap <String, NavigableSet<WordCount>> wcmap = new HashMap<>();
		
		Set <String> keys = map.keySet();
		for(String key : keys){
			TreeSet <WordCount> list = map.get(key);
			
			long tot = 0;
			for(WordCount w : list){
				tot += w.count;
			}
			
			TreeSet <WordCount> orderedList = new TreeSet<>(cc);
			orderedList.addAll(list);
			
			long tot2 = 0, thresh = (long) (tot * ep.TOT_PERC);
			WordCount first = orderedList.first(), last = orderedList.first();
			for(WordCount w : list){
				tot2 += w.count;
				last = w;
				if(tot2 > thresh) {
					break;
				}
			}
			
			NavigableSet <WordCount> mostUsedWords = orderedList.subSet(first, true, last, true);
			wcmap.put(key, mostUsedWords);
		}
		
		
		//ora per ogni parola del lessico, ho le parole piu' utilizzate con essa, in wcmap
		
		List <String> newDictionaryWords = new ArrayList<String>();
		List <List <Integer>> newAttributeValues = new ArrayList <List <Integer>>();
		
		newDictionaryWords.addAll(hashWords); //il lessico espanso contiene tutti i termini del vecchio dizionario...
		
		for(String key : keys){ //...piu' i termini piu' usati con ogni vecchio termine
			NavigableSet <WordCount> mostUsedWords = wcmap.get(key);
			
			for(WordCount c : mostUsedWords){
				if(!newDictionaryWords.contains(c.word)){ //no doppioni
					newDictionaryWords.add(c.word);
				}
			}
		}
		
		for(int i = 0 ; i < newDictionaryWords.size() ; i++){ //per ogni parola del nuovo dizionario
			String newWord = newDictionaryWords.get(i);
			
			if(i < words.length){ //se questa parola faceva parte del vecchio lessico
				List <Integer> y = new ArrayList<Integer>(); //allora ci rimetto gli stessi attributi
				for(int u = 0 ; u < attributeValues[i].length ; u++){
					y.add(attributeValues[i][u]);
				}
				newAttributeValues.add(y);
			} else { //altrimenti ci metto la media degli attributi delle parole del vecchio lessico con le quali compare
				newAttributeValues.add(getMeanArray(getRows(wcmap, newWord)));
			}
			
		}
		
		return new Dictionary(this.attributes, newDictionaryWords, newAttributeValues);
		
	}

	/**
	 * Salva questo lessico su file, utilizzando il formato appropriato.
	 * @param filename Il nome del file
	 * @throws java.io.IOException 
	 */
	public void saveToFile(String filename) throws IOException{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.print("attributi:\n");

		for (String attribute : attributes) {
			out.print(attribute + "\n");
		}
		
		out.print("\nparole:\n\n");
		
		for(int i = 0 ; i < words.length ; i++){ //il costruttore mi assicura che words.length == attributeValues.length
			out.print(words[i] + " ");
			for(int j = 0 ; j < attributeValues[i].length ; j++){
				out.print(attributeValues[i][j] + " ");
			}
			out.print("\n");
		}
		
		out.close();
	}
	
	/**
	 * Confronta questo lessico con quello passato come argomento.
	 * @param d
	 * @return
	 * @throws DictionaryException 
	 */
	public DictionaryComparison diff(Dictionary d) throws DictionaryException{	
		return new DictionaryComparison(this, d);
	}
	
	/**
	 * Espande questo lessico con i tweet caricati, in vari modi.
	 * Si suppone che i tweet passati come argomenti siano gia stati preprocessati (MainWindow.preprocessTweets()).
	 * @param tweets
	 */
	public void runExpansionTests(List <SingleTweet> tweets){
		List <SingleTweet> tweets1 = new ArrayList<>();
		List <SingleTweet> tweets2 = new ArrayList<>();
		
		Iterator<SingleTweet> i = tweets.iterator();
		boolean choice = true;
		while(i.hasNext()){
			if(choice) tweets1.add(i.next());
			else tweets2.add(i.next());
			choice = !choice;
		}
		
		WordUsage wu1 = new WordUsage(this, tweets1);
		WordUsage wu2 = new WordUsage(this, tweets2);

		ExpansionParameters ep1 = new ExpansionParameters(wu1, 0.02);
		ExpansionParameters ep2 = new ExpansionParameters(wu2, 0.02);

		try {
			Dictionary d1 = this.expand(ep1);
			Dictionary d2 = this.expand(ep2);

			DictionaryComparison dc = d1.diff(d2);
			
			System.out.println(dc.toString());
		} catch (DictionaryException e) {

		}
	}
	
	public String getTweetMoodAsString(String tweetText){
		double [] mood = getTweetMood(tweetText);
		
		if(mood == null) {
			return "Messaggio non valutabile";
		}
		
		String stringMood;
		
		stringMood = "Umore: (";
		
		for(int i = 0 ; i < attributes.length ; i++){
			stringMood += attributes[i] + " = " +  mood[i];
			if(i < attributes.length - 1) {
				stringMood += ", ";
			}
		}
		
		stringMood += ")\n";
		
		return stringMood;
	}
	
	/**
	 * Ritorna un array contenente l'umore del tweet per ogni attributo di
	 * umore presente nel lessico.
	 * Se il tweet non contiene parole del lessico, ritorna null.
	 * @param tweetText
	 * @return
	 */
	public double[] getTweetMood(String tweetText) {
		String [] tweetWords = StringUtils.getTweetWords(tweetText);
		int [] wordOccurrences = new int[words.length];
		double [] mood = new double[attributeValues[0].length];
		int tot = 0;
		
		for(int i = 0 ; i < wordOccurrences.length ; i++) {
			wordOccurrences[i] = 0;
		}
		
		for(int i = 0 ; i < mood.length ; i++) {
			mood[i] = 0;
		}
		
		
		for(String w : tweetWords){ //per ogni parola del tweet
			for(int i = 0 ; i < words.length ; i++){ //se corrisponde con la parola i-esima del lessico
				if(words[i].equals(w)){
					wordOccurrences[i]++; //allora incremento il suo numero di occorrenze
					tot++;
				}
			}
		}
		
		if(tot == 0) {
			return null;
		}
		
		//umore = (c1 * attributi1 + c2 * attributi2 + ... + cn * attributin) / (c1 + c2 + ... + cn)
		//dove ci = numero di occorrenze della parola i-esima
		//e attributii = array attributi parola i-esima
		for(int i = 0 ; i < wordOccurrences.length ; i++){
			for(int j = 0 ; j < wordOccurrences[i] ; j++) {
				mood = ArrayMath.arraySum(mood, attributeValues[i]);
			}
		}
		
		return ArrayMath.arrayDivide(mood, tot);
	}
	

	/**
	 * Ritorna una lista contenente le righe nelle quali compare la parola specificata.
	 * @param wcmap
	 * @param newWord
	 * @return
	 */
	private List<Integer> getRows(HashMap<String, NavigableSet<WordCount>> wcmap, String newWord) {
		List <Integer> rows = new ArrayList<>();
		
		Set <String> keys = wcmap.keySet();
		for(String key : keys){
			NavigableSet <WordCount> row = wcmap.get(key);
			
			for(WordCount w : row){
				if(w.word.equals(newWord)){ //vuol dire che newWord e' una delle parole piu utilizzate con key
					int rn = getRow(key); //prendo la riga dove sta key in questo dizionario
					if(rn>=0){
						rows.add(rn); //la aggiungo
					} else {
						System.out.println("This shouldn't happen");
					}
				}
			}
		}
		//System.out.println("La parola " + newWord + " si trova molto spesso con le parole numero " + rows + " del dizionario");
		return rows;
	}
	
	private int getRow(String word){
		for(int i = 0 ; i < words.length ; i++){
			if(word.endsWith(words[i]))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Ritorna una lista di interi che rappresenta un array risultante
	 * dalla media degli array riga di attributeValues, sulle righe
	 * specificate.
	 * @param pick Righe di cui fare la media
	 * @return
	 */
	private List <Integer> getMeanArray(List <Integer> rows){
		double [] result = new double [attributeValues[0].length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = 0;
		}
		
		for(int r : rows){ //calcolo l'array somma
			for(int j = 0 ; j < attributeValues[r].length ; j++) {
				result[j] += attributeValues[r][j];
			}
		}
		
		for(int i = 0 ; i < result.length ; i++){ //divido per il numero di array per fare la media
			result[i] /= rows.size();
		}
		
		List <Integer> res = new ArrayList<>(); //riverso l'array su una lista
		for(double i : result) {
			res.add((int)i);
		}
		
		return res;
	}
	
	/**
	 * Ritorna un nuovo lessico creato perturbando questo lessico.
	 * La perturbazione d deve essere compresa tra 0 e 1.
	 * @return
	 */
	public Dictionary addPerturbation(double d) throws DictionaryException {
		if(d < 0 || d > 1) {
			return null;
		}
		
		Random random = new Random();
		
		Dictionary ret = new Dictionary(this);
		
		for(int i = 0 ; i < attributeValues.length ; i++){
			for(int j = 0 ; j < attributeValues[i].length ; j++){
				/**
				 * Valore corrente: attributeValues[i][j]
				 * Nuovo valore in intervallo [min, max]
				 * x in intervallo [0, 1)
				 */
				int min = Math.max((int)(attributeValues[i][j] * (1-d)), 0);
				int max = Math.min((int)(attributeValues[i][j] * (1+d)), 100);
				double x = random.nextDouble();
				ret.attributeValues[i][j] = (int) (min + x* ((double)(max - min)));
			}
		}
				
		return ret;
	}
}
