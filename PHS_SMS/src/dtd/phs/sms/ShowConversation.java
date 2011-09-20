package dtd.phs.sms;

import android.content.Intent;
import android.os.Bundle;
import dtd.phs.sms.data.DataCenter;
import dtd.phs.sms.data.DataWrapper;
import dtd.phs.sms.data.IDataGetter;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.util.Logger;
 

public class ShowConversation
	extends PHS_SMSActivity
	implements IDataGetter
{

	public static final String INPUT_BUNDLE = "input_bundle";
	private static final int WAITING_FRAME = 0;
	private int threadId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    extractInputData();
	    bindViews();	    	    
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
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onResume() {	
		super.onResume();
		requestMessages();
	}

	private void requestMessages() {
		// TODO Auto-generated method stub
		showOnlyView(WAITING_FRAME);
		DataCenter.requestMessagesForThread(threadId, this);
	}

	@Override
	public void onGetDataFailed(Exception exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDataSuccess(DataWrapper wrapper) {
		// TODO Auto-generated method stub
		
	}

}
