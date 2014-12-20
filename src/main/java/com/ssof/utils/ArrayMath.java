package com.ssof.utils;

public class ArrayMath {
	/**
	 * Ritorna l'array ottenuto sommando i due array passati come argomento.
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static double[] arraySum(double[] a1, int[] a2) {
		if(a1.length != a2.length) {
			throw new RuntimeException(
				"Errore nella arraySum() in Dictionary.java. Array di lunghezza diversa non possono essere sommati");
		}
		
		double [] result = new double[a1.length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = a1[i] + a2[i];
		}
		
		return result;
	}
	
	/**
	 * Ritorna l'array ottenuto sommando i due array passati come argomento.
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static double[] arraySum(double[] a1, double[] a2) {
		if(a1.length != a2.length) {
			throw new RuntimeException(
				"Errore nella arraySum() in Dictionary.java. Array di lunghezza diversa non possono essere sommati");
		}
		
		double [] result = new double[a1.length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = a1[i] + a2[i];
		}
		
		return result;
	}
	
	/**
	 * Ritorna l'array ottenuto facendo la sottrazione vettoriale a1-a2.
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static double[] arraySub(double[] a1, int[] a2) {
		if(a1.length != a2.length)
			throw new RuntimeException("Errore nella arraySub() in Dictionary.java. Array di lunghezza diversa non possono essere sommati");
		
		double [] result = new double[a1.length];
		
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = a1[i] - a2[i];
		}
		
		return result;
	}
	
	/**
	 * Ritorna l'array ottenuto dividendo l'array per lo scalare div.
	 * @param array
	 * @param div
	 * @return
	 */
	public static double[] arrayDivide(double[] array, double div) {
		double [] result = new double[array.length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = array[i] / div;
		}
		
		return result;
	}
	
	/**
	 * Ritorna l'array ottenuto moltiplicando l'array per lo scalare mult.
	 * @param array
	 * @param div
	 * @return
	 */
	public static double[] arrayMultiply(double[] array, double mult) {
		double [] result = new double[array.length];
		
		for(int i = 0 ; i < result.length ; i++){
			result[i] = array[i] * mult;
		}
		
		return result;
	}
}
