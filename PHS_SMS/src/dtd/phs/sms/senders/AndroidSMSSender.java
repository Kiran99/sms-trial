package dtd.phs.sms.senders;

import dtd.phs.sms.data.entities.MessageItem;

public class AndroidSMSSender implements INormalMessageSender {

	private INormalMessageSenderListener listener;

	public AndroidSMSSender(INormalMessageSenderListener listener) {
		this.listener = listener;
	}

	@Override
	public void send(MessageItem mess) {
		
	}

}
