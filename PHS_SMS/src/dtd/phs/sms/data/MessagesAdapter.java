package dtd.phs.sms.data;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dtd.phs.sms.R;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.util.Helpers;

public class MessagesAdapter extends BaseAdapter {

	public class Holder {

		public TextView tvMessage;

	}

	private static final int ME_COLOR = 0xee87ceeb;
	private static final int FRIEND_COLOR = 0xeee0ffff;

	private SMSList messages;
	private Context context;

	public MessagesAdapter(SMSList messages, Context context) {
		this.messages = messages;
		this.context = context;
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
			v = Helpers.inflate(R.layout.message_item, null, context);
			holder = new Holder();
			holder.tvMessage = (TextView)v.findViewById(R.id.tvMessage);
			v.setTag(holder);
		} else {
			holder = (Holder) v.getTag();
		}
		displayMessage(position, holder);
		return v;
	}

	private void displayMessage(int position, Holder holder) {
		SMSItem smsItem = messages.get(position);
		String body = smsItem.getBody();
		long personId = smsItem.getPersonId();
		if ( personId == 0 ) {
			body = "Me: " + body;
//			holder.tvMessage.setBackgroundColor(ME_COLOR);
		} else {
//			holder.tvMessage.setBackgroundColor(FRIEND_COLOR);
			body = "Friend: " + body;
		}
		holder.tvMessage.setText(body);
	}

}
