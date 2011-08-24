package dtd.phs.sms;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import dtd.phs.sms.data.DataCenter;
import dtd.phs.sms.data.DataWrapper;
import dtd.phs.sms.data.IDataGetter;
import dtd.phs.sms.data.IListFactory;
import dtd.phs.sms.data.SummariesListFactory;

public class ShowInbox 
	extends PHS_SMSActivity
	implements IDataGetter
{
	private BaseAdapter adapter;
	private ListView listview;
	private IListFactory adapterFactory;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);        
        adapterFactory = new SummariesListFactory();
        bindViews();
        loadData();
    }


	private void bindViews() {
		listview = (ListView) findViewById(R.id.list);
	}

	/**
	 * Load all summary:
	 * - Summary include: Contact + { thread ids } + { most recent messages | Draft }
	 */
	private void loadData() {
		DataCenter.requestSummaries(this);
	}

	@Override
	public void onGetDataFailed(Exception exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDataSuccess(DataWrapper wrapper) {		
		
		adapter = adapterFactory.createAdapter( wrapper.getData() );
		listview.setAdapter( adapter );
		listview.setOnItemClickListener( adapterFactory.createOnItemClickListener(wrapper.getData()) );
	}

	
}