package dtd.phs.sms.senders;

public interface ISMS_SendListener {
	public void onSendIMessageSuccess(Object data);
	public void onSendIMessageFailed(Object data);
}
