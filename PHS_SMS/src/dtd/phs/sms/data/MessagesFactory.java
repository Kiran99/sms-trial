package dtd.phs.sms.data;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.ui.OnMessageItemClickListener;

public class MessagesFactory implements IListFactory {

	@Override
	public BaseAdapter createAdapter(Object object) {
		//should the type be MessagesList  - not SMSList - because, later we have both SMS and I-SMS 
		return new MessagesAdapter((SMSList) object);
	}

	@Override
	public OnItemClickListener createOnItemClickListener(
			Activity act,
			BaseAdapter adapter) {
		MessagesAdapter mAdapter = (MessagesAdapter) adapter;
		return new OnMessageItemClickListener(act,mAdapter);
	}

}
