package dtd.phs.sms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import dtd.phs.sms.global.UIThreadHandler;

public class PHS_SMSActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		UIThreadHandler.getInstance();
	}
}
