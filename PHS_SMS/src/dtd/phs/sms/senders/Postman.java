package dtd.phs.sms.senders;

import dtd.phs.sms.data.entities.MessageItem;

//TODO: All this must be called inside an service + new thread
public class Postman 
	implements 
		ISMS_SendListener,
		INormalMessageSenderListener
{
	
	private SendMessageListener messageListener;

	public void sendMessage(MessageItem message, SendMessageListener listener) {
		setListener(listener);
		tryToSendIMessage(message);
	}

	private void tryToSendIMessage(MessageItem message) {
		ISMSSender iSender = new GoogleSender(this);
		iSender.send( message );
	}
	private void setListener(SendMessageListener listener) {
		this.messageListener = listener;
	}


	private void tryToSendNormalMessage(MessageItem mess) {
		INormalMessageSender sender = new AndroidSMSSender(this);
		sender.send( mess );
	}

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
	
	@Override
	public void onNormalMessageSendFailed(Object data) {
		messageListener.onSendSuccces(data);
	}

	@Override
	public void onNormalMessageSendSuccess(Object data) {
		messageListener.onSendFailed( data );
	}

}
