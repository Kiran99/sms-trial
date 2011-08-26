package dtd.phs.sms.data.entities;

import java.io.InputStream;
import java.sql.Date;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import dtd.phs.sms.R;
import dtd.phs.sms.global.ApplicationContext;
import dtd.phs.sms.util.Logger;

public class SummaryItem {

	static public class TimeComparator implements Comparator<SummaryItem> {
		@Override
		public int compare(SummaryItem object1, SummaryItem object2) {
			long diff  = object1.getTimeMillis() - object2.getTimeMillis();
			if ( diff < 0 ) return 1;
			if ( diff > 0 ) return -1;
			return 0;
		}

	}

	private static final String UNKNOWN = "Unknown";
	private static final long UNKNOWN_PERSON_ID = -1L;
	private static Bitmap STUB_CONTACT_BITMAP = null;
	private static final int STUB_CONTACT_RES_ID = R.drawable.icon;

	private Uri avatarURI;
	private String contactName;
	private int messagesCount;
	private String latestTime;
	private String latestActionMessage;
	private long latestTimeMillis;
	private long personId;
	private String contactNumber;

	public SummaryItem(SMSList smsList) {
		//TODO: fill the fields
		//TODO: better performance : sort the SMSList according to time (by the time creating map)
		//TODO: better performance : just run one for loop, and check everything else in that loop
		this.avatarURI = null;

		this.personId = getPerson(smsList);
		//TODO: tidy up here !
		if ( toBeShowID() ) {
			Context context = ApplicationContext.getInstance(null);
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(ContentUris.withAppendedId(RawContacts.CONTENT_URI,personId), null, null, null, null);
			if ( cursor.moveToFirst() ){
				String[] columnNames = cursor.getColumnNames();			
				HashMap<String, String> contactValues = new HashMap<String, String>();
				for(String cname : columnNames ) {
					try {
						contactValues.put(cname, cursor.getString(cursor.getColumnIndex(cname)));
					} catch (Exception e) {
						Logger.logException(e);
					}
				}

				Logger.logInfo("ID = "+ personId + " -- " + contactValues.toString());	

			}
			cursor.close();
		}


		this.contactName = getAContact(smsList);
		this.contactNumber = getNumber(smsList);


		this.messagesCount = smsList.size();

		SMSItem sms = getLatestSMS( smsList );
		this.latestTimeMillis = sms.getDate();
		this.latestActionMessage = sms.getBody();
		this.latestTime = createDateTimeString(latestTimeMillis);

	}


	static final long[] TO_BE_SHOWNED_ID = {441,448,226};  
	private boolean toBeShowID() {
		for(long ID : TO_BE_SHOWNED_ID) {
			if (personId == ID) return true;
		}
		return false;
	}


	public InputStream getContactPhotoStream() {
		Context context = ApplicationContext.getInstance(null);
		if (personId > 0 ) {
			String contactId = getContactID();
			Uri contactURI = Uri.withAppendedPath(Contacts.CONTENT_URI,contactId);
			try {
				return ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactURI);
			} catch (Exception e) {
				Logger.logException(e);
				return null;
			}
		} else return null;
	}


	private String getContactID() {
		Context context = ApplicationContext.getInstance(null);
		ContentResolver cr = context.getContentResolver();
		Uri personUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI,personId);
		Cursor cursor =  cr.query(personUri, new String[] {RawContacts.CONTACT_ID}, null, null, null);
		cursor.moveToFirst();
		String contactId = cursor.getString(cursor.getColumnIndex(RawContacts.CONTACT_ID));
		return contactId;
	}
	
	public Bitmap getContactPhoto() {
		InputStream is = getContactPhotoStream();
		if (is == null) {
			if (STUB_CONTACT_BITMAP == null) 
				STUB_CONTACT_BITMAP = BitmapFactory.decodeResource(ApplicationContext.getInstance(null).getResources(), STUB_CONTACT_RES_ID);
			return STUB_CONTACT_BITMAP;
		} else {
			return BitmapFactory.decodeStream(is);
		}
	}

	private long getPerson(SMSList smsList) {
		for(SMSItem sms : smsList) {
			if ( sms.getPerson() > 0 ) {
				return sms.getPerson();
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
			String name = sms.getAddress() + "::ID= " + personId;
			if ( name != null ) return name;
		}
		return UNKNOWN;
	}

	private String getNumber(SMSList smsList) {
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
