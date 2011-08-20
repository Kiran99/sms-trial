package dtd.phs.sms.data.entities;

import java.util.ArrayList;

import android.net.Uri;

public class SummariesList extends ArrayList<SummaryItem> {
	private static final long serialVersionUID = -4053587841265625274L;

	public Uri getAvatarURI(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContactName(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMessagesCount(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLatestTime(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @return latest message OR draft
	 */
	public CharSequence getLatestActionMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
