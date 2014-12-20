package com.ssof.twitter;

import java.util.ArrayList;

import com.ssof.dbm.DBManager;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetReceiver implements StatusListener {
	/**
	 * Numero di tweet ricevuti prima di aggiungerli al database.
	 */
	private final int BUF_MAX_TWEETS = 100;
	
	private ArrayList <Status> tweetBuf;
	private long totalReceived;
	private DBManager dbManager;

	public TweetReceiver(DBManager dbManager){
		this.dbManager = dbManager;
		tweetBuf = new ArrayList<>();
		totalReceived = 0;
	}

	@Override
	public void onStatus(Status status) {
		System.out.println("Ricevuto tweet " + (++totalReceived) + " " + status.getText());
		
		tweetBuf.add(status);
		if(tweetBuf.size() >= BUF_MAX_TWEETS){
		    flush();
		}
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		
	}

	@Override
	public void onStallWarning(StallWarning stallWarning) {
		
	}

	/**
	 * Scrive tutti i tweet bufferizzati sul file.
	 */
	public void flush(){
		this.dbManager.addTweets(tweetBuf); //aggiungi tweets al database
		
		tweetBuf.clear(); //svuota il buffer
	}
}
