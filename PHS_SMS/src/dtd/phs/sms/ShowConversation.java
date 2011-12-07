package dtd.phs.sms;

import java.util.ArrayList;
import java.util.List;

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
import dtd.phs.sms.data.MessagesFactory;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.global.ThreadPools;
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
	private ListView listview;
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
		listview = (ListView) findViewById(R.id.list);

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

	public void onGetDataFailed(Exception exception) {
		Logger.logException(exception);
	}

	public void onGetDataSuccess(DataWrapper wrapper) {
		updateListView(wrapper);
		updateContactsInfo(wrapper.getData());
		
	}

	private void updateContactsInfo(final Object data) {
		//TODO: this shouldn't be here ! UI should not have knowledge about inside of database
		ThreadPools.getInstance().add(new Runnable() {
			
			public void run() {
				SMSList messages = (SMSList) data;
				loadContactsFromMessages( messages );
			}

			private void loadContactsFromMessages(SMSList messages) {
				List<Long> personIds = getAllPersonIds(messages);				
				ArrayList<String> contactIds = getContactIds(personIds);
				
				
			}

			private List<Long> getAllPersonIds(SMSList messages) {
				List<Long> ids = new ArrayList<Long>();
				//TODO: what happens with thread contains only sent sms (no
				for(SMSItem item : messages) {
					long personId = item.getPerson();
					if ( personId > 0 ) {
						ids.add(personId);
					}
				}
				return ids;
			}

			private ArrayList<String> getContactIds(List<Long> personIds) {
				ArrayList<String> contactsId = new ArrayList<String>();
				for(long personId : personIds) {
					String contactId = SMSItem.getContactID(personId);
					boolean dup = false;
					for(int i = 0 ; i < contactsId.size(); i++)
						if ( contactsId.get(i).equals(contactId) ) {
							dup = true;
							contactsId.set(i, contactId);
							break;
						} 
					if ( ! dup ) contactsId.add(contactId);
				}
				return contactsId;
			}
		});
		
	}

	private void updateListView(DataWrapper wrapper) {
		adapter = adapterFactory.createAdapter(wrapper.getData());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(adapterFactory.createOnItemClickListener(this, adapter));
		listview.setSelection(adapter.getCount());
		showOnlyView(DATA_FRAME);
	}
	

}
