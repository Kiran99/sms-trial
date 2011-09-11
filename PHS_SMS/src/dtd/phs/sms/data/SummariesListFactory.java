package dtd.phs.sms.data;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import dtd.phs.sms.data.entities.SummariesList;
import dtd.phs.sms.ui.OnSumaryItemClickListener;
import dtd.phs.sms.ui.SummariesAdapter;

public class SummariesListFactory implements IListFactory {


	@Override
	public BaseAdapter createAdapter(Object object) {
		return new SummariesAdapter((SummariesList) object);
	}

	@Override
	public OnItemClickListener createOnItemClickListener(Activity act,BaseAdapter adapter) {
		SummariesAdapter sumAdapter = (SummariesAdapter) adapter;
		SummariesList summaries = sumAdapter.getSummaries();
		return new OnSumaryItemClickListener(act, summaries);
	}

}
