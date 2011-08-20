package dtd.phs.sms.global;

import android.os.Handler;

public class UIThreadHandler extends Handler {
	private static volatile UIThreadHandler instance = null;
	private UIThreadHandler() {
		super();
	}
	public static UIThreadHandler getInstance() {
		if ( instance == null ) {
			instance = new UIThreadHandler();
		}
		return instance;
	}

}
