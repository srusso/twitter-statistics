package com.ssof.twitter;

public class TweetManager {
	private final TweetReceiverThread tweetReceiverThread;
	private Thread receiverThread = null;

	public TweetManager(TweetReceiverThread tweetReceiverThread){
		this.tweetReceiverThread = tweetReceiverThread;
	}
	
	public boolean isListening(){
		return receiverThread != null;
	}
	
	public void startListening(){
		if(receiverThread != null){
			return;
		}
		
		receiverThread = new Thread(this.tweetReceiverThread);
		
		receiverThread.start();
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
			if(receiverThread!=null) {
				receiverThread.join();
			}
		} catch (InterruptedException e1) {
			
		}
		
		receiverThread = null;
		
		startListening();
	}
}
