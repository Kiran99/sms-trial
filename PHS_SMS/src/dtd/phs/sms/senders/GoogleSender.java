package dtd.phs.sms.senders;

import android.content.Context;
import android.content.Intent;
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
		Intent xmppServiceIntent = new Intent(context,XMPPService.class);
		XMPPService.messageToSend = message;		
		context.startService(xmppServiceIntent);
	}

}
