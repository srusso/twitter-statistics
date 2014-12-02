package com.ssof.twitter;

import java.util.ArrayList;
import ts.dbm.DBManager;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetReceiver implements StatusListener{
	/**
	 * Numero di tweet ricevuti prima di aggiungerli al database.
	 */
	private final int BUF_MAX_TWEETS = 100;
	
	private ArrayList <Status> tweetBuf;
	private long totalReceived;
	
	public TweetReceiver(){
		tweetBuf = new ArrayList<Status>();
		totalReceived = 0;
	}
	
	
	public void onStatus(Status status) {
		System.out.println("Ricevuto tweet " + (++totalReceived) + " " + status.getText());
		
		tweetBuf.add(status);
		if(tweetBuf.size() >= BUF_MAX_TWEETS){
		    flush();
		}
		
	}
	
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	
	public void onException(Exception ex) {
		ex.printStackTrace();
	}
    
	public void onScrubGeo(long userId, long upToStatusId) {
		
	}
	
	
	/**
	 * Scrive tutti i tweet bufferizzati sul file.
	 */
	public void flush(){
		DBManager dbm = DBManager.getInstance();
		
		dbm.addTweets(tweetBuf); //aggiungi tweets al database
		
		tweetBuf.clear(); //svuota il buffer
	}
}
