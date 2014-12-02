package com.ssof.emotions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import ts.exceptions.DictionaryException;

public class DictionaryComparison {
	/**
	 * Array che rappresenta la differenza media, in valore assoluto,
	 * tra gli attributi (in comune) dei due dizionari.
	 */
	private final double [] meanAbsDiffArray;
	
	/**
	 * Numero di parole che compaiono nel primo dizionario ma non nel secondo.
	 */
	private final int exclWords1;
	
	/**
	 * Numero di parole che compaiono nel secondo dizionario ma non nel primo.
	 */
	private final int exclWords2;
	
	/**
	 * Numero di parole che compaiono in entrambi i dizionari.
	 */
	private final int commonWords;
	
	private final String [] attributes;
	
	private final double nonCommonWordRatio;
	
	private final double meanOfMeanDiffArray;
	
	public DictionaryComparison(Dictionary d1, Dictionary d2) throws DictionaryException{
		//ora controllo se i due lessici hanno gli stessi attributi
		if(d1.attributes.length != d2.attributes.length){
			throw new DictionaryException("Impossibile confrontare i due lessici: l'elenco degli attributi e' diverso");
		}

		for(int i = 0 ; i < d1.attributes.length ; i++){
			if(!d1.attributes[i].equals(d2.attributes[i]))
				throw new DictionaryException("Impossibile confrontare i due lessici: l'elenco degli attributi e' diverso");
		}


		//ok, i lessici hanno gli stessi attributi qui


		List <String> w1 = new ArrayList<String>(); //contiene le parole che compaiono solo nel lessico d1
		List <String> w2 = new ArrayList<String>(); //contiene le parole che compaiono solo nel lessico d2
		List <String> commonWords = new ArrayList<String>(); //parole in comune tra d1 e d2

		for(String word : d1.words){ //prendo le parole che compaiono in d1 ma non in d2
			if(!d2.containsWord(word)){
				w1.add(word);
			} else commonWords.add(word);
		}

		for(String word : d2.words){ //prendo le parole che compaiono in d2 ma non in d1
			if(!d1.containsWord(word)){
				w2.add(word);
			}
		}

		double [] sumArray1 = getSumArray(d1, commonWords);
		double [] sumArray2 = getSumArray(d2, commonWords);
		double [] diffArray = arrayDifference(sumArray1, sumArray2); //fa sumArray1 - sumArray2
		double [] absArray  = arrayAbs(diffArray); //ritorna l'array valore assoluto
			
		this.meanAbsDiffArray    = arrayDivide(absArray, commonWords.size());
		this.exclWords1          = w1.size();
		this.exclWords2          = w2.size();
		this.commonWords         = commonWords.size();
		this.attributes          = d1.attributes;
		this.nonCommonWordRatio  = (((double)exclWords1 + (double)exclWords2) / (double)this.commonWords);
		this.meanOfMeanDiffArray = getMeanOfArray(meanAbsDiffArray);
		
	}
	
	public String toString(){
		String str;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		
		str = "Attributi: ";
		
		for(int i = 0 ; i < attributes.length ; i++){
			if(i < attributes.length-1)
				str += attributes[i] + ", ";
			else str += attributes[i] + "\n";
		}
		
		str += "Numero di parole in comune tra i due lessici: " + commonWords + "\n";
		str += "Numero di parole che compaiono solo nel primo lessico: " + exclWords1 + "\n";
		str += "Numero di parole che compaiono solo nel secondo lessico: " + exclWords2 + "\n";
		str += "Differenza media su ogni attributo: (";
		for(int i = 0 ; i < meanAbsDiffArray.length ; i++){
				str += nf.format(meanAbsDiffArray[i]);
			if(i < meanAbsDiffArray.length - 1)
				str += ", ";
			else str += ")";
		}
		str += "\nMedia delle differenze medie su ogni attributo: " + nf.format(meanOfMeanDiffArray) + "\n";
		str += "Rapporto tra somma parole non in comune e parole in comune: " + nf.format(nonCommonWordRatio) + "\n";
		
		return str;
	}
	
	/**
	 * Ritorna la media delle differenze medie su ogni attributo,
	 * solo sugli attributi in comune tra i due lessici.
	 * @return
	 */
	public double getMeanOfMeanDiffArray(){
		return meanOfMeanDiffArray;
	}
	
	/**
	 * Ritorna il rapporto tra la somma delle parole non in comune e il numero di parole in comune
	 * Due lessici hanno k parole in comune, n parole esclusive del primo lessico e m parole esclusive
	 * del secondo, questa funzione ritorna (n+m)/k
	 * @return
	 */
	public double getNotCommonRatio(){
		return nonCommonWordRatio;
	}
	
	private double getMeanOfArray(double[] array) {
		double result = 0;
		
		for(int i = 0 ; i < array.length ; i++){
			result += array[i];
		}
		
		result /= array.length;
		
		return result;
	}
	
	/**
	 * Divide un array per il vettore div.
	 * @param absArray
	 * @param size
	 * @return
	 */
	private double[] arrayDivide(double[] array, double div) {
		double [] result = new double[array.length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = array[i] / div;
		}
		
		return result;
	}

	private double[] arrayAbs(double[] array) {
		double [] result = new double[array.length];
		
		for(int i = 0 ; i < result.length ; i++){
			if(array[i] >= 0){
				result[i] = array[i];
			} else {
				result[i] = -array[i];
			}
		}
		
		return result;
	}

	/**
	 * Ritorna l'array dato da a1 - a2
	 * @param a1
	 * @param a2
	 * @return
	 */
	private double[] arrayDifference(double[] a1, double[] a2) {
		if(a1.length != a2.length)
			throw new RuntimeException("Errore nella getArrayDifference() in Dictionary.java. Array di lunghezza diversa non possono essere sottratti");
		
		double [] result = new double[a1.length];
		
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = a1[i] - a2[i];
		}
		
		return result;
	}

	/**
	 * Ritorna un array dato dalla somma dei valori degli attributi,
	 * considerando solo le righe che corrispondono alle parole specificate dalla lista.
	 * @param rows
	 * @return
	 */
	private double[] getSumArray(Dictionary d, List<String> rows) {
		double [] result = new double [d.attributeValues[0].length];
		
		for(int i = 0 ; i < result.length ; i++)
			result[i] = 0;
		
		for(int i = 0 ; i < d.words.length ; i++){ //per ogni parola del lessico
			if(rows.contains(d.words[i])){ //se quella parola e' stata selezionata
				for(int j = 0 ; j < d.attributeValues[i].length ; j++){ //aggiungi la riga dei suoi valori all'array result
					result[j] += d.attributeValues[i][j];
				}
			}
		}		
		
		return result;
	}
}
