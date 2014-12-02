package com.ssof.twitter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ts.datatypes.TimePeriod;

public class SingleTweet {
	public final long millisSinceEpoch;
	public final boolean isRetweet;
	public final double latitude;
	public final double longitude;
	public final String text;
	public final String user;
	public final String source;
	public final String place;
	
	public SingleTweet(long millisSinceEpoch, boolean isRetweet, double latitude, double longitude, String text, String user, String source, String place){
		this.millisSinceEpoch = millisSinceEpoch;
		this.isRetweet = isRetweet;
		this.latitude = latitude;
		this.longitude = longitude;
		this.text = text;
		this.user = user;
		this.source = source;
		this.place = place;
	}
	
	public Calendar getDate() {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(millisSinceEpoch);
		return c;
	}
	
	/**
	 * Domenica == 0
	 * ..
	 * ..
	 * Sabato == 6
	 * @return
	 */
	public int getDayOfWeek(){
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(millisSinceEpoch);
		
		return c.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public long getMillisSinceEpoch() {
		return millisSinceEpoch;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getUser() {
		return user;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public String getPlace() {
		return place;
	}
	
	/**
	 * Ritorna true se questo tweet � stato pubblicato nell'intervallo
	 * specificato. Ritorna false altrimenti.
	 * @param tp
	 * @return
	 */
	public boolean containedInTimeInterval(TimePeriod tp){
		return tp.contains(millisSinceEpoch);
	}
}
