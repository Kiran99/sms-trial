package dtd.phs.sms.data.entities;

import java.io.InputStream;
import java.sql.Date;
import java.util.Comparator;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import dtd.phs.sms.global.ApplicationContext;

public class SummaryItem {

	static public class TimeComparator implements Comparator<SummaryItem> {
		@Override
		public int compare(SummaryItem object1, SummaryItem object2) {
			long diff  = object1.getTimeMillis() - object1.getTimeMillis();
			if ( diff < 0 ) return -1;
			if ( diff > 0 ) return 1;
			return 0;
		}

	}

	private static final String UNKNOWN = "Unknown";
	private static final long UNKNOWN_PERSON_ID = -1L;

	private Uri avatarURI;
	private String contactName;
	private int messagesCount;
	private String latestTime;
	private String latestActionMessage;
	private long latestTimeMillis;
	private long personId;

	public SummaryItem(SMSList smsList) {
		//TODO: fill the fields
		//TODO: better performance : sort the SMSList according to time (by the time creating map)
		//TODO: better performance : just run one for loop, and check everything else in that loop
		this.avatarURI = null;
		this.contactName = getAContact(smsList);
		this.messagesCount = smsList.size();
		SMSItem sms = getLatestSMS( smsList );
		this.latestTimeMillis = sms.getDate();
		this.latestActionMessage = sms.getBody();
		this.latestTime = createDateTimeString(latestTimeMillis);
		this.personId = getPersonId(smsList);
	}
	

	public InputStream getPhotoStream(long personId) {
		
		Context context = ApplicationContext.getInstance(null);
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, personId);
		Uri photoUri = Uri.withAppendedPath(context.getContentResolver(), contactUri)
		ContactsContract.Contacts.openContactPhotoInputStream(ApplicationContext.getInstance(null).getContentResolver(), URIUtils.)

	}
	private long getPersonId(SMSList smsList) {
		for(SMSItem sms : smsList) {
			if ( sms.getPersonId() > 0 ) {
				return sms.getPersonId();
			}
		}
		return UNKNOWN_PERSON_ID;
	}

	private String createDateTimeString(long latestTimeMillis2) {
		Date date = new Date(latestTimeMillis2);
		return date.toLocaleString();
	}

	private SMSItem getLatestSMS(SMSList smsList) {
		if ( smsList.isEmpty() ) return null;
		SMSItem latestSMS = smsList.get(0);
		for(SMSItem sms : smsList) {
			if ( sms.getDate() > latestSMS.getDate() ) {
				latestSMS = sms;
			}
		}
		return latestSMS;
	}

	private String getAContact(SMSList smsList) {
		for(SMSItem sms : smsList) {
			String name = sms.getAddress();
			if ( name != null ) return name;
		}
		return UNKNOWN;
	}

	public long getLatestTimeMillis() {
		return latestTimeMillis;
	}

	public void setLatestTimeMillis(long latestTimeMillis) {
		this.latestTimeMillis = latestTimeMillis;
	}

	public void setAvatarURI(Uri avatarURI) {
		this.avatarURI = avatarURI;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setMessagesCount(int messagesCount) {
		this.messagesCount = messagesCount;
	}

	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}

	public void setLatestActionMessage(String latestActionMessage) {
		this.latestActionMessage = latestActionMessage;
	}
	public Uri getAvatarURI() {
		return avatarURI;
	}

	public long getTimeMillis() {
		return latestTimeMillis;
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
