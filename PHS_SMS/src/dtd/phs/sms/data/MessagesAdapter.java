package dtd.phs.sms.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import dtd.phs.sms.R;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.util.Helpers;

public class MessagesAdapter extends BaseAdapter {

	public class Holder {

		public TextView tvMessage;
		public LinearLayout layout;

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
//		if ( v == null ) {
			v = Helpers.inflate(R.layout.message_item, null, context);
			holder = new Holder();
			holder.tvMessage = (TextView)v.findViewById(R.id.tvMessage);
			holder.layout = (LinearLayout) v.findViewById(R.id.layout_message_item);
			v.setTag(holder);
//		} else {
//			holder = (Holder) v.getTag();
//		}
		displayMessage(position, holder);
		return v;
	}

	private void displayMessage(int position, Holder holder) {
		SMSItem smsItem = messages.get(position);
		String body = smsItem.getBody();
		long personId = smsItem.getPersonId();
		NinePatchDrawable d = null;
		if ( personId == 0 ) {
			body = "Me: " + body;
			d = (NinePatchDrawable)context.getResources().getDrawable(R.drawable.buble_left);
			holder.layout.setGravity(Gravity.LEFT);
//			holder.tvMessage.setBackgroundColor(ME_COLOR);
		} else {
			d = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.buble_right);
//			holder.tvMessage.setBackgroundColor(FRIEND_COLOR);
			body = "Friend: " + body;
			holder.layout.setGravity(Gravity.RIGHT);			
		}
		holder.tvMessage.setBackgroundDrawable(d);
		holder.tvMessage.setText(body);
	}

}
