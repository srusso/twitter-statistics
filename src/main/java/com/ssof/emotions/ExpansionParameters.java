package com.ssof.emotions;


import com.ssof.exceptions.WrongExpansionParametersException;

/**
 * Parametri di espansione di un lessico.
 * @author Simone
 *
 */
public class ExpansionParameters {
	/**
	 * Quando viene espanso un lessico, per ogni parola del lessico si ha la lista delle parole utilizzate con essa.
	 * Per ognuna di tali parole, si ha il numero di co-occorrenze con la parola del lessico.
	 * Questo parametro viene utilizzato come segue:
	 * 		Vengono aggiunte al nuovo lessico tutte le parole che co-occorrono piu' spesso
	 * 		con la parola del vecchio lessico. Le parole vengono aggiunte finche' la somma
	 * 		delle loro co-occorrenze e' minore di TOT_PERC*cooccorrenze totali.
	 */
	public final double TOT_PERC;
	
	/**
	 * Contiene la lista di parole che occorrono con ogni parola del lessico di partenza.
	 */
	public final WordUsage wordUsage;
	
	public ExpansionParameters(WordUsage wordUsage, double tot_perc) throws WrongExpansionParametersException{
		if(tot_perc <= 0 || tot_perc >= 1)
			throw new WrongExpansionParametersException("Il parametro percentuale deve essere compreso tra 0 e 1 esclusi");
		
		this.TOT_PERC  = tot_perc;
		this.wordUsage = wordUsage;
	}
}
