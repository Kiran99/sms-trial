package dtd.phs.sms.message_center;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import dtd.phs.sms.data.entities.MessageItem;

public class AndroidSMSSender implements INormalMessageSender {

	private INormalMessageSenderListener listener;
	private Context context;
	static private final String SENT = "SMS_SENT";
	static private final String DELIVERED = "SMS_DELIVERED";
	public enum ResultCode {DELIVERED,SENT,FAILED, GENERIC_FAILURE, NO_SERVICE, NULL_PDU, RADIO_OFF};
	

	public AndroidSMSSender(INormalMessageSenderListener listener,Context context) {
		this.listener = listener;
		this.context = context;
	}

	@Override
	public void send(final MessageItem mess) {
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);
		
		//Note: Be careful : listener.onNormalMessageSendSuccess() called 2 times (1 sent, 1 delivered)
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					mess.setResultCode(ResultCode.SENT);
					listener.onNormalMessageSendSuccess(mess);
					return;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					mess.setResultCode(ResultCode.GENERIC_FAILURE);
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					mess.setResultCode(ResultCode.NO_SERVICE);
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					mess.setResultCode(ResultCode.NULL_PDU);
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					mess.setResultCode(ResultCode.RADIO_OFF);
					break;
				}
				listener.onNormalMessageSendFailed(mess);
				
			}
		},new IntentFilter(SENT));

		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					mess.setResultCode( ResultCode.DELIVERED );
					listener.onNormalMessageSendSuccess(mess);
					break;
				case Activity.RESULT_CANCELED:
					mess.setResultCode(ResultCode.FAILED);
					listener.onNormalMessageSendFailed(mess);
					break;
				}
			}
		},new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(mess.getNumber(), null, mess.getContent(), sentPI, deliveredPI);
	}

}
