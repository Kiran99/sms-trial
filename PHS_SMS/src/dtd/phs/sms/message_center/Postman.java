package dtd.phs.sms.message_center;

import android.content.Context;
import dtd.phs.sms.data.entities.MessageItem;

//TODO: All this must be called inside an service + new thread
public class Postman 
implements 
ISMS_SendListener,
INormalMessageSenderListener
{

	private SendMessageListener messageListener;
	private Context context;

	public Postman(Context context) {
		this.context = context;
	}

	public void sendMessage(MessageItem message, SendMessageListener listener, boolean forceSendNormalSMS) {
		//TODO: ping the receiver (friend) before send "real" message, if the friend is connected then:
		// let forceSendNormalSMS = false - The connectivity will be checked again inside the sender,
		// but it decrease the chance user have to wait too long to send a "normal" message to a friend
		// which doesn't use G-Message
		if ( ! forceSendNormalSMS ) {
			setListener(listener);
			tryToSendIMessage(message);
		} else {
			tryToSendNormalMessage(message);
		}
	}

	private void tryToSendIMessage(MessageItem message) {
		ISMSSender iSender = new GoogleSender(this, context);
		iSender.send( message );
	}
	private void setListener(SendMessageListener listener) {
		this.messageListener = listener;
	}

	private void tryToSendNormalMessage(MessageItem mess) {
		INormalMessageSender sender = new AndroidSMSSender(this,context);
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
			tryToSendNormalMessage(mess);
		}
	}
	
	/**
	 * END
	 * I-MESSAGE - Interface: ISMS_SendListener
	 */

	
	@Override
	public void onNormalMessageSendFailed(Object data) {
		messageListener.onSendSuccces(data);
	}

	@Override
	public void onNormalMessageSendSuccess(Object data) {
		messageListener.onSendFailed( data );
	}


}
