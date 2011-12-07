package dtd.phs.sms.senders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import dtd.phs.sms.data.entities.MessageItem;

public class GoogleSender implements ISMSSender {

	private ISMS_SendListener listener;
	private Context context;

	public GoogleSender(ISMS_SendListener listener, Context context) {
		this.listener = listener;
		this.context = context;
	}

	@Override
	public void send(MessageItem message) {
		Intent xmppServiceIntent = new Intent(context,GoogleXMPPService.class);
		GoogleXMPPService.messageToSend = message;
		context.registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
			}
		},new IntentFilter(GoogleXMPPService.TIME_OUT_I_MESSAGE));
		context.startService(xmppServiceIntent);
		
	}

}
