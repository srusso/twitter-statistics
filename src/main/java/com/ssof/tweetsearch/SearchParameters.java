package com.ssof.tweetsearch;

import com.ssof.exceptions.DateFormatException;
import com.ssof.utils.DateUtils;
import com.ssof.utils.StringUtils;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Classe che rappresenta i parametri di ricerca da utilizzare per effettuare
 * le ricerche sui tweet caricati in memoria.
 * Un tweet soddisfa i parametri specificati in questa classe solo se li soddisfa tutti.
 */
public class SearchParameters {
	private final DateUtils dateUtils;
	
	/**
	 * Parametro soddisfatto se il tweet contiene almeno una delle parole contenute in questa lista.
	 */
	Collection <String> oneOrMoreWords = null;
	
	/**
	 * Parametro soddisfatto se il tweet contiene tutte le parole contenute in questa lista.
	 */
	Collection <String> allWords = null;
	
	/**
	 * Parametro soddisfatto se il tweet contiene questa esatta stringa.
	 */
	String exactString = null;
	
	/**
	 * Parametro soddisfatto se il tweet e' stato creato dopo la data specificata.
	 */
	DateTime afterDate = null;
	
	/**
	 * Parametro soddisfatto se il tweet e' stato creato prima della data specificata.
	 */
	DateTime beforeDate = null;
	
	/**
	 * Parametro soddisfatto se l'autore del tweet e' quello specificato da questa stringa. [case insensitive]
	 */
	String author = null;
	
	/**
	 * Parametro soddisfatto se la localita' del tweet e' quella specificata da questa stringa. [case insensitive]
	 */
	String place = null;
	
	/**
	 * Parametro soddisfatto se la sorgente del tweet [web, Twitter for iPhone..] e' quella specificata da questa stringa. [case insensitive]
	 * Case insensitive.
	 */
	String source = null;
	
	/**
	 * Crea dei parametri di ricerca vuoti, soddisfatti quindi da ogni tweet.
	 */
	public SearchParameters(){
		this.dateUtils = new DateUtils();
	}
	
	/**
	 * Crea dei parametri di ricerca con i parametri specificati.
	 * Ognuno dei parametri puo' essere null.
	 * In tal caso, il parametro non viene preso in considerazione nella ricerca.
	 * Nel caso del parametro caseSensitive, null equivale a false.
	 * 
	 * Il risultato delle ricerche con questi parametri sara' una lista di tweet che soddisfano tutti i parametri specificati.
	 * 
	 * @param oneormore Stringa contenente delle parole, almeno una delle quali deve essere presente nei tweet ritornati dalla ricerca
	 * @param all Stringa contenente delle parole, le quali devono essere tutte presenti nei tweet ritornati dalla ricerca
	 * @param exact Stringa che deve essere contenuta nei tweet ritornati dalla ricerca
	 * @param dopoData Stringa nella forma gg/mm/aaaa o gg-mm-aaaa o g/m/aaaa o g-m-aaaa che indica che i tweet ritornati dalla ricerca devono essere
	 * stati generati dopo la data specificata
	 * @param primaData Stringa nella forma gg/mm/aaaa o gg-mm-aaaa o g/m/aaaa o g-m-aaaa che indica che i tweet ritornati dalla ricerca devono essere
	 * stati generati prima della data specificata
	 * @param autore Indica che i tweet ritornati dalla ricerca devono essere dell'autore specificato [case insensitive]
	 * @param localita Indica che i tweet ritornati dalla ricerca devono provenire dalla localita specificata da questa stringa [case insensitive]
	 * @param sorgente Indica che i tweet ritornati dalla ricerca devono avere la sorgente specificata da questa stringa [case insensitive]
	 * @throws DateFormatException 
	 */
	public SearchParameters(String oneormore, String all, String exact, String dopoData, String primaData, String autore, String localita, String sorgente) throws DateFormatException {
		this.dateUtils = new DateUtils();
		
		if(oneormore != null && oneormore.length() != 0){
			String [] words = oneormore.split(" ");
			oneOrMoreWords = new TreeSet<>();
			for(String word : words){
				oneOrMoreWords.add(StringUtils.removeAccents(word));
			}
		}
		
		if(all != null && all.length() != 0){
			String [] words = all.split(" ");
			allWords = new TreeSet<>();
			for(String word : words){
				allWords.add(StringUtils.removeAccents(word));
			}
		}
		
		if(exact != null && exact.length() != 0){
			exactString = StringUtils.removeAccents(exact);
		}
		
		if(dopoData != null && dopoData.length() != 0){
			this.afterDate = this.dateUtils.translateStringDate(dopoData);
		}
		
		if(primaData != null && primaData.length() != 0){
			this.beforeDate = this.dateUtils.translateStringDate(primaData);
		}
		
		if(autore != null && autore.length() != 0){
			this.author = StringUtils.removeAccents(autore);
		}
		
		if(localita != null && localita.length() != 0){
			this.place = StringUtils.removeAccents(localita);
		}
		
		if(sorgente != null && sorgente.length() != 0){
			this.source = StringUtils.removeAccents(sorgente);
		}
	}
	
}
