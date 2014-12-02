package com.qaprosoft.qa.util;

public class SleepUtil {
    public static void sleep(int timeInSec) {
	try {
	    Thread.sleep(timeInSec * 1000);
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }
}
