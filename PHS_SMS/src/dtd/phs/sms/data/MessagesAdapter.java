package dtd.phs.sms.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dtd.phs.sms.R;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.util.Helpers;

public class MessagesAdapter extends BaseAdapter {

	public class Holder {

		public TextView tvMessage;

	}

	private SMSList messages;

	public MessagesAdapter(SMSList messages) {
		this.messages = messages;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Holder holder = null;
		if ( v == null ) {
			v = Helpers.inflate(R.layout.message_item, null);
			holder = new Holder();
			holder.tvMessage = (TextView)v.findViewById(R.id.tvMessage);
			v.setTag(holder);
		} else {
			holder = (Holder) v.getTag();
		}
		
		holder.tvMessage.setText( messages.get(position).getBody());
		return v;
	}

}
