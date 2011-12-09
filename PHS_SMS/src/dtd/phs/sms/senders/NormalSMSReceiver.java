package dtd.phs.sms.senders;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.global.ThreadPools;
import dtd.phs.sms.util.Helpers;
import dtd.phs.sms.util.Logger;

public class NormalSMSReceiver extends BroadcastReceiver {

	private static final String ANDROID_SMS_RECEIVE_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static final String  GENERAL_MESSAGE_RECEIVED = "dtd.phs.gsms.general_message.received";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		//---get the SMS message passed in---
		Logger.logInfo(" Message received ");
		if (intent.getAction().equals(ANDROID_SMS_RECEIVE_ACTION)) {
			Logger.logInfo(" Android mesage received !");
		} else if (intent.getAction().equals(GoogleXMPPService.I_MESSAGE_RECEIVED)) {
			Logger.logInfo(" I-mesage received !");
			ThreadPools.getInstance().add(new Runnable() {

				@Override
				public void run() {
					final String from = Helpers.revertUsername2PhoneNumber( intent.getStringExtra(GoogleXMPPService.EXTRA_SENDER));
//					String id = intent.getStringExtra(GoogleXMPPService.EXTRA_MESSAGE_ID);
					final String message = intent.getStringExtra(GoogleXMPPService.EXTRA_MESSAGE_BODY);
					ContentValues values = new ContentValues();
					values.put(SMSItem.ADDRESS, from);
					values.put(SMSItem.BODY, message);
					context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
				}
			});

		} else {
			Logger.logInfo(" Weird action received !");
		}

		broadcastNewMessageComing(context);


	}

	/*
	 * Just notify that there is a new message is coming, 
	 * doesn't provide any more information
	 */
	private void broadcastNewMessageComing(Context context) {
		Intent intent = new Intent();
		intent.setAction(GENERAL_MESSAGE_RECEIVED);
		context.sendBroadcast(intent);
	}
}