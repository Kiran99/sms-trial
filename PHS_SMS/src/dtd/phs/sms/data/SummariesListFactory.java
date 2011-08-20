package dtd.phs.sms.data;

import dtd.phs.sms.data.entities.SummariesList;
import dtd.phs.sms.ui.adapters.SummariesAdapter;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SummariesListFactory implements IListFactory {


	@Override
	public BaseAdapter createAdapter(Object object) {
		return new SummariesAdapter((SummariesList) object);
	}

	@Override
	public OnItemClickListener createOnItemClickListener(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
