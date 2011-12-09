package dtd.phs.sms.message_center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import dtd.phs.sms.data.entities.MessageItem;
import dtd.phs.sms.util.Logger;

public class GoogleSender implements ISMSSender {

	private ISMS_SendListener listener;
	private Context context;

	public GoogleSender(ISMS_SendListener listener, Context context) {
		this.listener = listener;
		this.context = context;
	}

	@Override
	public void send(final MessageItem message) {
		Intent xmppServiceIntent = new Intent(context,GoogleXMPPService.class);
		long currentTime = System.currentTimeMillis();
		message.setId(""+currentTime);		
		GoogleXMPPService.messageToSend = message;



		//if the message cannot be sent ontime, it does mean that it cannot be sent ! 
		// so, sometimes there will be duplicates (sent by I-SMS, but the reply comes too late
		// and it will be sent by normal sms manager 
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int errorCode = intent.getIntExtra(GoogleXMPPService.ERROR_CODE, GoogleXMPPService.UNKNOWN_ERROR);

				//TODO:
				switch (errorCode) {
				case GoogleXMPPService.CONNECTION_ERROR:					
					break;
				case GoogleXMPPService.AUTHENTICATION_ERROR:
					break;
				case GoogleXMPPService.I_MESSAGE_TIME_OUT:
					Logger.logInfo("Time out !");
					break;
				case GoogleXMPPService.UNKNOWN_ERROR:
					break;
				}

				listener.onSendIMessageFailed(message);

			}
		},new IntentFilter(GoogleXMPPService.XMPP_FAILURE));

		context.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String id = intent.getStringExtra(GoogleXMPPService.EXTRA_MESSAGE_ID);
				message.setId( id );
				listener.onSendIMessageSuccess(message);
			}
		}, new IntentFilter(GoogleXMPPService.I_MESSAGE_DELIVERED));

		//THis is sender , not receiver
//		context.registerReceiver(new BroadcastReceiver() {
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				listener.onReceiveIMessage(getData(intent));
//			}
//
//			private Object getData(Intent intent) {
//				return null;
//			}
//		},new IntentFilter(GoogleXMPPService.I_MESSAGE_RECEIVED));

		context.startService(xmppServiceIntent);

	}

	@Override
	public void startInternetPostmanService(Context context) {
		Intent i = new Intent(context, GoogleXMPPService.class);
		GoogleXMPPService.messageToSend = null;
		context.startService(i);
	}

}
