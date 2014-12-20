package com.ssof.emotions;

import com.ssof.exceptions.DictionaryException;
import com.ssof.exceptions.DictionaryFileFormatException;
import com.ssof.exceptions.WrongAttributeValueException;
import com.ssof.exceptions.WrongNumberOfValuesException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe che serve per leggere un file contenente una lista di parole
 * e la loro classificazione secondo diversi parametri.
 *
 */
public class LoadDictionary {
	
	private final static int STATE_START = 0;
	private final static int STATE_READ_ATTR = 1;
	private final static int STATE_READ_WORD = 2;
	
	/**
	 * Legge un file di testo e ne estrae e restituisce il lessico.
	 * 
	 * @return Un'istanza di Dictionary
	 * @throws WrongNumberOfValuesException Se ad una parola sono stati assegnati piu o meno attributi di quelli dichiarati all'inizio
	 * @throws WrongAttributeValueException Se ad una parola e' stato assegnato un valore non intero oppure intero ma fuori dal range valido
	 * @throws java.io.IOException Se ci sono problemi nell'apertura o lettura dal file
	 * @throws DictionaryFileFormatException Se c'e' qualche errore nel formato del file
	 * @throws DictionaryException Se c'e' un errore nella creazione del dizionario
	 */
	public static Dictionary loadDictionary(File file) throws WrongNumberOfValuesException, WrongAttributeValueException, IOException, DictionaryFileFormatException, DictionaryException{
		return loadDictionary(file.getAbsolutePath());
	}
	
	/**
	 * Legge un file di testo e ne estrae e restituisce il lessico.
	 * 
	 * @return Un'istanza di Dictionary
	 * @throws WrongNumberOfValuesException Se ad una parola sono stati assegnati piu o meno attributi di quelli dichiarati all'inizio
	 * @throws WrongAttributeValueException Se ad una parola e' stato assegnato un valore non intero oppure intero ma fuori dal range valido
	 * @throws java.io.IOException Se ci sono problemi nell'apertura o lettura dal file
	 * @throws DictionaryFileFormatException Se c'e' qualche errore nel formato del file
	 * @throws DictionaryException Se c'e' un errore nella creazione del dizionario
	 */
	public static Dictionary loadDictionary(String filename) throws WrongNumberOfValuesException, WrongAttributeValueException, IOException, DictionaryFileFormatException, DictionaryException, DictionaryException {
		FileReader inFile = new FileReader(filename);
		BufferedReader in = new BufferedReader(inFile);
		int currentLine, state;
		String line;
		
		List <String> attributes = new ArrayList<>();
		List <String> wordList = new ArrayList<>();
		List <List <Integer>> attributeValues = new ArrayList <>();
		
		for(currentLine = 1, state = STATE_START ; true ; currentLine++ ){
			line = in.readLine(); //legge una riga del file, togliendo i caratteri di a capo
			
			if(line == null){ //se il file e' finito
				break;
			}
			
			//ignoro tutte le linee vuote o che iniziano con #
			if(line.length() == 0 || line.startsWith("#"))
				continue;
			
			String [] words = line.split("[ ]+");
			if(words.length == 0)
				continue;
			
			if(state == STATE_START){
				if(words.length != 1){
					closeResources(in, inFile);
					throw new DictionaryFileFormatException("Errore nella riga " + currentLine + ". Mi aspettavo \'attributi:\', invece ho trovato \'" + line + "\'.");
				} else if(!words[0].equalsIgnoreCase("attributi:")){
					closeResources(in, inFile);
					throw new DictionaryFileFormatException("Errore nella riga " + currentLine + ". Mi aspettavo \'attributi:\', invece ho trovato \'" + line + "\'.");
				} else {
					state = STATE_READ_ATTR;
				}
			} else if(state == STATE_READ_ATTR){
				if(words.length == 1){ //se sto aspettando un attributo e leggo una riga di una sola parola
					if(words[0].equalsIgnoreCase("parole:")){
						state = STATE_READ_WORD;
					} else {
						attributes.add(words[0]);
					}
				} else { //se sto aspettando un attributo e leggo una riga con piu di una parola
					closeResources(in, inFile);
					throw new DictionaryFileFormatException("Errore nella riga " + currentLine + ". Mi aspettavo \'parole:\' oppure un attributo \'" + line + "\'.");
				}
			} else { //state == STATE_READ_WORD
				if(words.length != attributes.size() + 1){
					closeResources(in, inFile);
					throw new WrongNumberOfValuesException(words.length - 1, attributes.size(), filename, currentLine);
				} else {
					int [] values = new int[attributes.size()];
					
					for(int i = 1 ; i < words.length ; i++){
						try {
							values[i-1] = Integer.parseInt(words[i]);							
						} catch (NumberFormatException e){
							closeResources(in, inFile);
							throw new DictionaryFileFormatException("Errore nella riga " + currentLine + ". Trovato argomento non numerico \'" + words[i] + "\'.");
						}
						if(values[i-1] < 0 || values[i-1] > 100){
							closeResources(in, inFile);
							throw new WrongAttributeValueException(filename, currentLine, 0, 100, values[i-1], attributes.get(i-1));
						}
					}
					
					wordList.add(words[0]);
					
					List <Integer> temp = new ArrayList <>();
					for(int v : values) {
						temp.add(v);
					}
					
					attributeValues.add(temp);
				}
			}
			
		}
		
		closeResources(in, inFile);
		return new Dictionary(attributes, wordList, attributeValues);
	}
	
	private static void closeResources(BufferedReader b, FileReader f) throws IOException{
		b.close();
		f.close();
	}
}
