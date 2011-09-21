package dtd.phs.sms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import dtd.phs.sms.data.DataCenter;
import dtd.phs.sms.data.DataWrapper;
import dtd.phs.sms.data.IDataGetter;
import dtd.phs.sms.data.IListFactory;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.util.Logger;


public class ShowConversation
extends PHS_SMSActivity
implements IDataGetter
{

	public static final String INPUT_BUNDLE = "input_bundle";
	private static final int DATA_FRAME = 0;
	private static final int WAITING_FRAME = 1;
	
	private int threadId;
	private ImageView ivAvatar;
	private TextView tvContactName;
	private TextView tvNumber;
	private ListView lvMessages;
	private EditText etMessage;
	private TextView tvCount;
	private Button btSend;
	private Button btAttach;
	
	private IListFactory adapterFactory;
	private BaseAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_conversation);
		adapterFactory = new MessagesFactory();
		extractInputData();
		bindViews();	    	    
	}

	@Override
	protected void onResume() {	
		super.onResume();
		requestMessages();
	}

	private void extractInputData() {
		Intent srcIntent = getIntent();
		Bundle bundleExtra = srcIntent.getBundleExtra(INPUT_BUNDLE);
		if ( bundleExtra == null ) {
			showInbox();
		} else {
			Logger.logInfo("No bundle parameter is given !");
			threadId = bundleExtra.getInt(SMSItem.THREAD_ID);
		}
	}

	private void showInbox() {
		Intent i = new Intent( this,ShowInbox.class);
		startActivity(i);
		finish();
	}

	private void bindViews() {
		//Top bar
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		tvContactName = (TextView) findViewById(R.id.tvContactName);
		tvNumber = (TextView) findViewById(R.id.tvNumber);

		//listview
		lvMessages = (ListView) findViewById(R.id.list);

		//Bottom
		etMessage = (EditText) findViewById(R.id.etMessage);
		tvCount = (TextView) findViewById(R.id.tvCount);
		btSend = (Button) findViewById(R.id.btSend);
		btAttach = (Button) findViewById(R.id.btAttach);
	}



	private void requestMessages() {
		showOnlyView(WAITING_FRAME);
		DataCenter.requestMessagesForThread(threadId, this);
	}

	private void showOnlyView(int id) {
		FrameLayout mainFrames = (FrameLayout) findViewById(R.id.main_frames);
		for(int i = 0 ; i < mainFrames.getChildCount() ; i++) {
			View v = mainFrames.getChildAt(i);
			if ( i == id ) 
				v.setVisibility(View.VISIBLE);
			else v.setVisibility(View.GONE);
		}
	}

	@Override
	public void onGetDataFailed(Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDataSuccess(DataWrapper wrapper) {
		adapter = adapterFactory.createAdapter(wrapper.getData());
		lvMessages.setAdapter(adapter);
		lvMessages.setOnItemClickListener(adapterFactory.createOnItemClickListener(this, adapter));
	}

}
