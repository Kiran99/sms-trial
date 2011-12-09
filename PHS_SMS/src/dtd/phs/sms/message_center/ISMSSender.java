package dtd.phs.sms.message_center;

import dtd.phs.sms.data.entities.MessageItem;

public interface ISMSSender {

	void send(MessageItem message);

}
