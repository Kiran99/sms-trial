package dtd.phs.sms.message_center;

public interface INormalMessageSenderListener {

	void onNormalMessageSendSuccess(Object data);
	void onNormalMessageSendFailed(Object data);
}
