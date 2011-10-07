package dtd.phs.sms.senders;

public interface INormalMessageSenderListener {

	void onNormalMessageSendSuccess(Object data);
	void onNormalMessageSendFailed(Object data);
}
