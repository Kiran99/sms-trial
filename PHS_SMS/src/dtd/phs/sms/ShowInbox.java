package dtd.phs.sms;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import dtd.phs.sms.data.DataCenter;
import dtd.phs.sms.data.DataWrapper;
import dtd.phs.sms.data.IDataGetter;
import dtd.phs.sms.data.IListFactory;
import dtd.phs.sms.data.SummariesListFactory;
import dtd.phs.sms.ui.SummariesAdapter;
import dtd.phs.sms.util.Logger;

public class ShowInbox 
	extends PHS_SMSActivity
	implements IDataGetter
{
	private static final int WAITING_FRAME = 0;
	private static final int DATA_FRAME = 1;
	private BaseAdapter adapter;
	private ListView listview;
	private IListFactory adapterFactory;
	private FrameLayout mainFrames;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);        
        adapterFactory = new SummariesListFactory();
        bindViews();
    }


    @Override
    protected void onResume() {
    	//Note: load data here, so the loader thread could be restarted every time the act is resumed
    	super.onResume();
    	loadData();
    }
	private void bindViews() {
		listview = (ListView) findViewById(R.id.list);
		mainFrames = (FrameLayout) findViewById(R.id.main_frames);
		showOnlyView(WAITING_FRAME);
	}

	private void showOnlyView(int id) {
		for(int i = 0 ; i < mainFrames.getChildCount() ; i++) {
			if ( i == id ) 
				mainFrames.getChildAt(i).setVisibility(View.VISIBLE);
			else mainFrames.getChildAt(i).setVisibility(View.INVISIBLE);
		}
	}


	/**
	 * Load all summary:
	 * - Summary include: Contact + { thread ids } + { most recent messages | Draft }
	 */
	private void loadData() {
		DataCenter.requestSummaries(this);
	}

	public void onGetDataFailed(Exception exception) {
		Logger.logException(exception);
	}

	public void onGetDataSuccess(DataWrapper wrapper) {		
		adapter = adapterFactory.createAdapter( wrapper.getData() );
		listview.setAdapter( adapter );
		listview.setOnItemClickListener( adapterFactory.createOnItemClickListener(this,adapter) );
		showOnlyView(DATA_FRAME);
	}
	
	@Override
	protected void onStop() {
		if ( adapter != null )
			((SummariesAdapter)adapter).stopLoadContacts();
		super.onStop();
	}

	
}