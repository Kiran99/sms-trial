package dtd.phs.sms.global;

import android.content.Context;

public class ApplicationContext {

	public static volatile Context context = null;
	public static Context getInstance(Context applicationContext) {
		if ( context == null ) {
			context = applicationContext;
		}
		return context;
	}

}
