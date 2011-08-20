package dtd.phs.sms;

import dtd.phs.sms.global.ApplicationContext;
import dtd.phs.sms.global.UIThreadHandler;
import android.app.Activity;
import android.os.Bundle;

public class PHS_SMSActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.getInstance(getApplicationContext());
		UIThreadHandler.getInstance();
	}
}
