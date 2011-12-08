package dtd.phs.sms.test;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import dtd.phs.sms.R;
import dtd.phs.sms.data.entities.SMSItem;

public class TestWriteSMS extends Activity {

	private EditText etNumber;
	private EditText etMessage;
	private Button btSave;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
	    setContentView(R.layout.test_write_sms);
	    
	    etNumber = (EditText) findViewById(R.id.etPhoneNumber);
	    etMessage = (EditText) findViewById(R.id.etMessage);
	    btSave = (Button) findViewById(R.id.btSave);
	    
	    btSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = etMessage.getText().toString();
				String number = etNumber.getText().toString();
				saveMessage(number,content);
			}
		});
	}

	private void saveMessage(String number, String content) {
		ContentValues values = new ContentValues();
		values.put(SMSItem.ADDRESS, number);
		values.put(SMSItem.BODY, content);
		getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}

}
