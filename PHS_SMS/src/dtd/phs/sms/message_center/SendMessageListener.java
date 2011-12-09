package dtd.phs.sms.message_center;

public interface SendMessageListener {

	void onSendSuccces(Object data);
	void onSendFailed(Object data);

}
