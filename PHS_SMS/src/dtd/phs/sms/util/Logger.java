package dtd.phs.sms.util;

import android.util.Log;

public class Logger {

	public static final boolean DEBUG_MODE = true; 
	private static final String TAG = "PHS_SMS";

	public static void logInfo(String extra) {
		if ( ! DEBUG_MODE ) return;
		Log.i(TAG, getLocation() + " -- extra: " + extra);
	}
	public static void logException(Exception e) {
		if ( ! DEBUG_MODE ) return;
		Log.e(TAG,getLocation()+ " -- Exception: " + e.getClass() + " == message: " + e.getMessage());
	}

	private static String getLocation() {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		String className = stackTraceElement.getClassName();
		String method = stackTraceElement.getMethodName();
		int line = stackTraceElement.getLineNumber();
		return className+"."+method+"::Line: "+line;
		
	}

}
