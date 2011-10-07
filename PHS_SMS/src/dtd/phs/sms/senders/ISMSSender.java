package dtd.phs.sms.senders;

import dtd.phs.sms.data.entities.MessageItem;

public interface ISMSSender {

	void send(MessageItem message);

}
