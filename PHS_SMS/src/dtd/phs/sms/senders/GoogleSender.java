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
		
		
		//if the message cannot be sent ontime, it does mean that it cannot be sent ! 
		// so, sometimes there will be duplicates (sent by I-SMS, but the reply comes too late
		// and it will be sent by normal sms manager 
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				listener.onSendIMessageFailed(null);
			}
		},new IntentFilter(GoogleXMPPService.TIME_OUT_I_MESSAGE));
		
		context.registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				listener.onSendIMessageSuccess(data);
			}
		}, new IntentFilter(GoogleXMPPService.I_MESSAGE_DELIVERED));
		
		context.startService(xmppServiceIntent);
		
	}

}
