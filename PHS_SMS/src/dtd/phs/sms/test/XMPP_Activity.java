package dtd.phs.sms.test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import dtd.phs.sms.R;
import dtd.phs.sms.data.entities.MessageItem;
import dtd.phs.sms.senders.GoogleXMPPService;

public class XMPP_Activity extends Activity {

	public static final String VO_GMAIL_ACC = "huongdoan12345@gmail.com";
	public static final String ME_GMAIL_ACC = "sphamhung@googlemail.com";
	
	private ListView lvMessages;
	private EditText etMessage;
	private Button btSend;
	protected ArrayList<String> displayedMessages;
	private ArrayAdapter<String> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.xmpp_test);
	    
	    lvMessages = (ListView) findViewById(R.id.lvMessages);
	    displayedMessages = new ArrayList<String>();
	    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, displayedMessages);
	    lvMessages.setAdapter(adapter);
	    
	    etMessage = (EditText) findViewById(R.id.etMessage);
	    
	    createButtonSend();

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String from  = intent.getStringExtra(GoogleXMPPService.EXTRA_SENDER);
				String body = intent.getStringExtra(GoogleXMPPService.EXTRA_MESSAGE_BODY);
				adapter.add(from+": " + body);
				adapter.notifyDataSetChanged();
			}
		}, new IntentFilter(GoogleXMPPService.I_MESSAGE_RECEIVED));
	}

	private void createButtonSend() {
		btSend = (Button) findViewById(R.id.btSend);
		btSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = etMessage.getText().toString();
				if (message.length() != 0 ) {
					MessageItem mess = new MessageItem();
					mess.setNumber(VO_GMAIL_ACC);
					mess.setContent(message);
					GoogleXMPPService.messageToSend = mess;
					startService(new Intent(XMPP_Activity.this, GoogleXMPPService.class));
					displayedMessages.add("Me: " + mess.getContent());
					adapter.notifyDataSetChanged();
				}
			}
		});
		
	}

	
	@Override
	protected void onDestroy() {
		stopService(new Intent(this,GoogleXMPPService.class));
		super.onDestroy();
	}
	public static final String VO_GMAIL_PWD = "quanduihoa";
	public static final String ME_GMAIL_PWD= "itrangdethuong";
	
}
