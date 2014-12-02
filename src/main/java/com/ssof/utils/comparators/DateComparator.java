package com.ssof.utils.comparators;

import java.util.Calendar;
import java.util.Comparator;

public class DateComparator implements Comparator<Calendar>{

	public int compare(Calendar o1, Calendar o2) {
		return o1.compareTo(o2);
	}	
	
}
