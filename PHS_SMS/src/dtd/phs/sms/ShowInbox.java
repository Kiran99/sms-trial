package dtd.phs.sms;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import dtd.phs.sms.data.DataCenter;
import dtd.phs.sms.data.DataWrapper;
import dtd.phs.sms.data.IDataGetter;
import dtd.phs.sms.data.IListFactory;
import dtd.phs.sms.data.SummariesListFactory;
import dtd.phs.sms.data.entities.SummariesList;

public class ShowInbox 
	extends PHS_SMSActivity
	implements IDataGetter
{
    private SummariesList summaries = new SummariesList();
	private BaseAdapter adapter;
	private ListView listview;
	private IListFactory factory;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);        
        factory = new SummariesListFactory();
        bindViews();
        loadData();
    }


	private void bindViews() {
		
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
		
		adapter = factory.createAdapter( wrapper.getData() );
		listview.setAdapter( adapter );
		listview.setOnItemClickListener( factory.createOnItemClickListener(wrapper.getData()) );
	}

	
}