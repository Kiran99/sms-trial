package dtd.phs.sms.message_center;

import android.content.Context;
import dtd.phs.sms.data.entities.MessageItem;

public interface ISMSSender {

	void send(MessageItem message);

	void startInternetPostmanService(Context context);

}
