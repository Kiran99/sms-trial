package dtd.phs.sms.data.entities;

import android.net.Uri;

public class SummaryItem {

	private Uri avatarURI;
	private String contactName;
	private int messagesCount;
	private String latestTime;
	private String latestActionMessage;

	public Uri getAvatarURI() {
		return avatarURI;
	}

	public String getContactName() {
		return contactName;
	}

	public int getMessagesCount() {
		return messagesCount;
	}

	public String getLatestTime() {
		return latestTime;
	}

	public String getLatestActionMessage() {
		return latestActionMessage;
	}

}
