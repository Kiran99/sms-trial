package dtd.phs.sms.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelpers {

	private static final String USERNAME = "PREF_USER_NAME";
	private static final String PASSWORD = "PREF_PASSWORD";
	private static final String FIRST_TIME_RUNNING = "PREF_FIRST_TIME";
	
	private static final String PREFENCE_FILE= "MY_PREFERENCES";

	public static void saveAccount(Context context, String username, String password) {
		savePreference(context, USERNAME, username);
		savePreference(context, PASSWORD, password);
	}
	
	public static String getUsername(Context context) {
		return getPreference(context, USERNAME);
	}

	public static String getPassword(Context context) {
		return getPreference(context, PASSWORD);
	}

	private static void savePreference(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFENCE_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private static String getPreference(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFENCE_FILE, Context.MODE_PRIVATE);
		String value = prefs.getString(key, null);
		return value;
	}

	public static boolean isFirstTimeRunning(Context applicationContext) {
		String mark = getPreference(applicationContext, FIRST_TIME_RUNNING);
		if ( mark == null ) return true;
		return false;
	}

	public static void turnOffFirstTimeRunning(Context applicationContext) {
		savePreference(applicationContext, FIRST_TIME_RUNNING, "false");
	}

}
