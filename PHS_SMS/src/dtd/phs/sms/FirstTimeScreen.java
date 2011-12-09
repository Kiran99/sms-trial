package dtd.phs.sms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import dtd.phs.sms.test.XMPP_Activity;
import dtd.phs.sms.util.Helpers;
import dtd.phs.sms.util.PreferenceHelpers;

public class FirstTimeScreen extends PHS_SMSActivity {

	private EditText etPhone;
	private Button btOk;
	private Button btCancel;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView( R.layout.first_time);
	    getViews();
	    attachActions2View();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (! PreferenceHelpers.isFirstTimeRunning(getApplicationContext()) ) {
			showInbox();
			finish();
		} 
	}
	private void showInbox() {
		Intent intent = new Intent(getApplicationContext(), XMPP_Activity.class);
		startActivity(intent);
	}

	private void attachActions2View() {
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = Helpers.generateUsername(etPhone.getText().toString());
				String password = Helpers.generatePassword(username);
				PreferenceHelpers.saveAccount(getApplicationContext(),username,password);
				showInbox();
				PreferenceHelpers.turnOffFirstTimeRunning(getApplicationContext());
				finish();
			}
		});
	}

	private void getViews() {
		etPhone = (EditText) findViewById(R.id.etPhone);
	    btOk = (Button) findViewById(R.id.btOk);
	    btCancel = (Button) findViewById(R.id.btCancel);
	}

}
