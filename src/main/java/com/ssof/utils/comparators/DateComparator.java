package com.ssof.utils.comparators;

import org.joda.time.DateTime;

import java.util.Comparator;

public class DateComparator implements Comparator<DateTime>{

	public int compare(DateTime o1, DateTime o2) {
		return o1.compareTo(o2);
	}	
	
}
