package dtd.phs.sms.message_center;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import dtd.phs.sms.data.entities.MessageItem;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.message_center.AndroidSMSSender.ResultCode;
import dtd.phs.sms.util.Helpers;
import dtd.phs.sms.util.Logger;
import dtd.phs.sms.util.PreferenceHelpers;

//TODO: All this must be called inside an service + new thread
public class Postman 
implements 
ISMS_SendListener,
INormalMessageSenderListener
{

	public static final String GENERAL_BEING_SENT_EVENT = "dtd.phs.sms.sms_being_sent";
	private static Postman instance;
	private SendMessageListener messageListener;
	private ISMSSender iSender;
	private INormalMessageSender sender;
	private Context context;

	private Postman(Context context) {
		this.context = context;
		iSender = new GoogleSender(this, context);
		sender = new AndroidSMSSender(this,context);
	}
	
	public static Postman getInstance(Context context) {
		if (instance == null) {
			instance = new Postman(context);			
		}
		return instance;
	}

	public void sendMessage(MessageItem message, SendMessageListener listener, boolean forceSendNormalSMS) {
		//TODO: ping the receiver (friend) before send "real" message, if the friend is connected then:
		// let forceSendNormalSMS = false - The connectivity will be checked again inside the sender,
		// but it decrease the chance user have to wait too long to send a "normal" message to a friend
		// which doesn't use G-Message
		saveSentMessage( message );
		if ( ! forceSendNormalSMS ) {
			setListener(listener);
			tryToSendIMessage(message);
		} else {
			tryToSendNormalMessage(message);
		}
	}

	/**
	 * Message is sent, but later there could be an error or delivered, whatever 
	 * @param message
	 */
	private void saveSentMessage(MessageItem message) {
		ContentValues values = new ContentValues();
		values.put(SMSItem.ADDRESS, message.getNumber());
		values.put(SMSItem.TYPE,""+SMSItem.MESSAGE_TYPE_SENT);
		values.put(SMSItem.PERSON_ID,"0");
		values.put(SMSItem.BODY, message.getContent());
		context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values );
		broadcastMessageIsBeingSent();
	}

	private void broadcastMessageIsBeingSent() {
		Intent i = new Intent();
		i.setAction(GENERAL_BEING_SENT_EVENT);
		context.sendBroadcast(i);
	}

	private void tryToSendIMessage(MessageItem message) {

		iSender.send( message );
	}
	private void setListener(SendMessageListener listener) {
		this.messageListener = listener;
	}

	private void tryToSendNormalMessage(MessageItem mess) {

		sender.send( mess );
	}
	/**
	 * I-MESSAGE - Interface: ISMS_SendListener
	 * BEGIN
	 */
	@Override
	public void onSendIMessageSuccess(Object data) {
		messageListener.onSendSuccces(data);
	}

	@Override
	public void onSendIMessageFailed(Object data) {
		if ( data instanceof MessageItem ) {
			MessageItem mess = (MessageItem) data;
			ResultCode errorCode = mess.getResultCode();
			if ( errorCode == ResultCode.I_MESSAGE_TIME_OUT ) {
				if ( ! mess.isPingMessage() ) { 
					tryToSendNormalMessage(mess);
				} else {
					//TODO:
					Logger.logInfo("TODO: process ping message");
				}
			}
		}
	}

	/**
	 * END
	 * I-MESSAGE - Interface: ISMS_SendListener
	 */


	@Override
	public void onNormalMessageSendFailed(Object data) {
		messageListener.onSendFailed( data );
	}

	@Override
	public void onNormalMessageSendSuccess(Object data) {
		messageListener.onSendSuccces(data);
	}

	public void startInternetPostman() {
		iSender.startInternetPostmanService(context);
	}

	public static String getServiceDomain() {
		return "@"+GoogleXMPPService.SERVICE;
	}


}
