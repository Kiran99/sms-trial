package dtd.phs.sms.data;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.ui.OnMessageItemClickListener;

public class MessagesFactory implements IListFactory {

	@Override
	public BaseAdapter createAdapter(Object object, Context context) {
		//should the type be MessagesList  - not SMSList - because, later we have both SMS and I-SMS 
		return new MessagesAdapter((SMSList) object, context);
	}

	@Override
	public OnItemClickListener createOnItemClickListener(
			Activity act,
			BaseAdapter adapter) {
		MessagesAdapter mAdapter = (MessagesAdapter) adapter;
		return new OnMessageItemClickListener(act,mAdapter);
	}

}
