package dtd.phs.sms;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendSMS extends Activity {

	protected static final String DEST_PNUM = "0977686056";
	private EditText etMessage;
	private Button btSend;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.send_sms);
	    etMessage = (EditText) findViewById(R.id.etMess);
	    btSend = (Button) findViewById(R.id.btSend);
	    btSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = etMessage.getText().toString();
				sendSMS(DEST_PNUM,text);
			}
		});
	}

	private void sendSMS(String destPnum, String text) {
		SmsManager smsMan = SmsManager.getDefault();
		smsMan.sendTextMessage(destPnum, null, text, null, null);
	}

}
