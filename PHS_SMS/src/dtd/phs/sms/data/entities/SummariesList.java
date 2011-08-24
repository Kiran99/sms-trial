package dtd.phs.sms.data.entities;

import java.util.ArrayList;

import android.net.Uri;

public class SummariesList extends ArrayList<SummaryItem> {
	private static final long serialVersionUID = -4053587841265625274L;

	public Uri getAvatarURI(int position) {
		return this.get(position).getAvatarURI();
	}

	public String getContactName(int position) {
		return this.get(position).getContactName();	
	}

	public int getMessagesCount(int position) {
		return this.get(position).getMessagesCount();
	}

	public String getLatestTime(int position) {
		return this.get(position).getLatestTime();
	}

	/**
	 * 
	 * @return latest message OR draft
	 */
	public String getLatestActionMessage(int position) {
		return this.get(position).getLatestActionMessage();	
	}

	/**
	 * 
	 * @param messages precondition: messages must be sorted by ThreadId
	 * @return list of message summaries
	 */
	public static SummariesList createFrom(SMSList messages) {
		SummariesList list = new SummariesList();
		return list;
	}

}
