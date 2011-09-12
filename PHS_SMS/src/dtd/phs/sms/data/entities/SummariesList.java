package dtd.phs.sms.data.entities;

import java.util.ArrayList;
import java.util.Collections;

import android.net.Uri;
import dtd.phs.sms.util.Logger;

public class SummariesList extends ArrayList<SummaryItem> {
	private static final long serialVersionUID = -4053587841265625274L;

	public Uri getAvatarURI(int position) {
		return this.get(position).getAvatarURI();
	}

	public int getMessagesCount(int position) {
		return this.get(position).getMessagesCount();
	}

	public String getLatestTime(int position) {
		return this.get(position).getLatestTime();
	}

	/**
	 * @return latest message OR draft
	 */
	public String getLatestActionMessage(int position) {
		return this.get(position).getLatestActionMessage();	
	}

	public static void sortByTime(SummariesList summaries) {
		Logger.logInfo(" summaries is sort !");
		Collections.sort(summaries, new SummaryItem.TimeComparator());
	}

	public String getContactId(int position) {
		return this.get(position).getContactId();
	}

	public String getContactNumber(int position) {
		return this.get(position).getContactNumber();	
	}

}
