package com.ssof.twitter;

import java.io.IOException;

import twitter4j.FilterQuery;
import twitter4j.StatusStream;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetReceiverThread implements Runnable {
	private class Restarter implements Runnable{
		TweetManager tm;
		
		public Restarter(TweetManager tm){
			this.tm = tm;	
		}
		
		public void run() {
			tm.restart();
		}
	}
	
	
	private final double [][] locations;
	private FilterQuery filter = new FilterQuery();
	
	private final TweetReceiver tweetReceiver;
	private StatusStream statusStream;
	private final TwitterStream twitterStream;
	
	private TweetManager tweetManager;
	
	private boolean abort = false;
	
	public TweetReceiverThread(TweetManager tweetManager) throws TwitterException{
		this.tweetManager = tweetManager;
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(TwitterCredentials.getConsumerKey())
		  .setOAuthConsumerSecret(TwitterCredentials.getConsumerSecret())
		  .setOAuthAccessToken(TwitterCredentials.getAccessToken())
		  .setOAuthAccessTokenSecret(TwitterCredentials.getAccessTokenSecret());
		
		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		
		tweetReceiver = new TweetReceiver();
		
		locations = new double [2][2];
		locations[0][0] = 7.852;   //longitudine sud ovest italia
		locations[0][1] = 37.645;  //latitudine sud ovest italia
		locations[1][0] = 13.1739; //longitudine nord est italia
		locations[1][1] = 46.58;   //latitudine nord est italia
		
		
		//cosi mi limito ai tweet italiani
		filter = new FilterQuery();
		filter.locations(locations);
		
		try{
			statusStream = twitterStream.getFilterStream(filter);
		} catch (TwitterException e){
			System.out.println("Errore nella creazione dello StatusStream [TweetReceiverThread.java]. Ricezione tweet annullata. Eccezione:\n" + e.toString());
			abort = true;
		}
		
		this.tweetManager = tweetManager;
	}

    public void run() {
    	boolean restart = false;
    	
    	if(abort){
    		tweetManager.receiverTerminated();
    		return;
    	}
    	
    	while(true){
    		if (Thread.interrupted()){
    			break;
    	    }
    		
			try {
				statusStream.next(tweetReceiver);
			} catch(IllegalStateException e){
				System.out.println("IllegalStateException");
				System.out.println(e);
				restart = true;
				break;
			} catch (TwitterException e) {
				//stream finito, esco
				System.out.println("TwitterException");
				System.out.println(e);
				break;
			}
		}
		
    	try {
			statusStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	System.out.println("Lettura stream interrotta, flusho i tweet rimanenti.");
		//tweetReceiver bufferizza i tweet e ne salva sul database molti insieme
		//con flush() forzo la scrittura sul db dei tweet ancora bufferizzati
		tweetReceiver.flush();
		
		if(restart){
			new Thread(new Restarter(tweetManager)).start();
		}
    }

}
