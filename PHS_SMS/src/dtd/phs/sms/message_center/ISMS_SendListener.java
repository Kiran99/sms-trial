package dtd.phs.sms.message_center;

public interface ISMS_SendListener {
	public void onSendIMessageSuccess(Object data);
	public void onSendIMessageFailed(Object data);
}
