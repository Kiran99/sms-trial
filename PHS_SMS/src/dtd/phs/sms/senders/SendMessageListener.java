package dtd.phs.sms.senders;

public interface SendMessageListener {

	void onSendSuccces(Object data);
	void onSendFailed(Object data);

}
