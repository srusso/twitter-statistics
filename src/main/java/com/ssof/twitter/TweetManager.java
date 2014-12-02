package com.ssof.twitter;

import twitter4j.TwitterException;


public class TweetManager {
	
	private Thread receiverThread = null;
	
	public TweetManager(){
		
	}
	
	public boolean isListening(){
		return receiverThread!=null;
	}
	
	public void startListening(){
		
		if(receiverThread != null){
			return;
		}
		
		try {
			receiverThread = new Thread(new TweetReceiverThread(this));
		} catch (TwitterException e) {
			System.err.println("Errore nella creazione dello StatusListenerManager. Eccezione:");
			e.printStackTrace();
		}
		
		if(receiverThread != null) receiverThread.start();
		else System.err.println("Impossibile avviare il thread listener.");
	}
	
	public void stopListening(){
		if(receiverThread == null){
			return;
		}
		
		receiverThread.interrupt();
		
		try {
			receiverThread.join();
		} catch (InterruptedException e1) {
			
		}
		
		receiverThread = null;
	}
	
	public void receiverTerminated(){
		receiverThread = null;
	}

	/**
	 * Riceve la notifica da parte del receiver thread che la ricezione
	 * e' stata interrotta a causa di un'eccezione.
	 */
	public void restart() {
		try {
			if(receiverThread!=null)
				receiverThread.join();
		} catch (InterruptedException e1) {
			
		}
		
		receiverThread = null;
		
		startListening();
	}
}
