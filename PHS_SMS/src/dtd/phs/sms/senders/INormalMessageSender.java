package dtd.phs.sms.senders;

import dtd.phs.sms.data.entities.MessageItem;

public interface INormalMessageSender {

	void send(MessageItem mess);

}
