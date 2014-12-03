package com.ssof.twitter;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetReceiverThread implements Runnable {
	private final TweetReceiver tweetReceiver;
	private final TwitterStream twitterStream;
	private final TweetManager tweetManager;
	
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

		final double[][] locations = new double[2][2];
		locations[0][0] = 7.852;   //longitudine sud ovest italia
		locations[0][1] = 37.645;  //latitudine sud ovest italia
		locations[1][0] = 13.1739; //longitudine nord est italia
		locations[1][1] = 46.58;   //latitudine nord est italia
		
		
		//cosi mi limito ai tweet italiani
		final FilterQuery filter = new FilterQuery();
		filter.locations(locations);

		twitterStream.addListener(tweetReceiver);
	}

    public void run() {
		twitterStream.sample();
    }

}
