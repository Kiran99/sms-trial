package dtd.phs.sms.data.entities;

import java.sql.Date;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
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
	//	private static Bitmap STUB_CONTACT_BITMAP = null;
	//	private static final int STUB_CONTACT_RES_ID = R.drawable.icon;

	private Uri avatarURI;
	private int messagesCount;
	private String latestTime;
	private String latestActionMessage;
	private long latestTimeMillis;
	private long personId;
	private String contactId;
	private String contactNumber;
	private int threadId;

	public SummaryItem(SMSList smsList) {
		//TODO: fill the fields
		//TODO: better performance : sort the SMSList according to time (by the time creating map)
		//TODO: better performance : just run one for loop, and check everything else in that loop

		this.threadId = genThreadId(smsList);
		this.personId = getPerson(smsList);

		if ( personId > 0 )
			this.contactId = SMSItem.getContactID(personId);

		if ( contactId != null ) {
			Uri contactURI = Uri.withAppendedPath(Contacts.CONTENT_URI, contactId);
			this.avatarURI = Uri.withAppendedPath(contactURI, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		}


		debugInfo();

		this.contactNumber = getNumber(smsList);

		this.messagesCount = smsList.size();

		SMSItem sms = getLatestSMS( smsList );
		this.latestTimeMillis = sms.getDate();
		this.latestActionMessage = sms.getBody();
		this.latestTime = createDateTimeString(latestTimeMillis);

	}


	private int genThreadId(SMSList smsList) {
		return smsList.get(0).getThreadId();
	}


	private void debugInfo() {
		if ( toBeShowID() ) {
			Context context = ApplicationContext.getInstance(null);
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(Uri.withAppendedPath(Contacts.CONTENT_URI,contactId), null, null, null, null);
//			Cursor cursor = contentResolver.query(Uri.withAppendedPath(RawContacts.CONTENT_URI,""+personId), null, null, null, null);
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
	}


	static final String[] TO_BE_SHOWNED_ID = {"218"};
	public static final String INTENT_KEY = "SummaryItem";  
	
	private boolean toBeShowID() {
		for(String ID : TO_BE_SHOWNED_ID) {
			if (contactId != null && contactId.equals(ID)) return true;
		}
		return false;
	}







	static public String getContactID(SMSList smsList) {
		long person = getPerson(smsList);
		return SMSItem.getContactID(person);
	}

	//	public InputStream getContactPhotoStream() {
	//		Context context = ApplicationContext.getInstance(null);
	//		if ( toBeShowID() ) {
	//			personId++;
	//			personId--;
	//		}
	//		if (personId > 0 ) {
	//			String contactId = this.contactId;
	//			Uri contactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,contactId);
	//			try {
	//				return ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactURI);
	//			} catch (Exception e) {
	//				Logger.logException(e);
	//				return null;
	//			}
	//		} else return null;
	//	}
	//	public Bitmap getContactPhoto() {
	//		InputStream is = getContactPhotoStream();
	//
	//		if (is == null) {
	//			if (STUB_CONTACT_BITMAP == null) 
	//				STUB_CONTACT_BITMAP = BitmapFactory.decodeResource(ApplicationContext.getInstance(null).getResources(), STUB_CONTACT_RES_ID);
	//			return STUB_CONTACT_BITMAP;
	//		} else {
	//			return BitmapFactory.decodeStream(is);
	//		}
	//	}

	//	private Bitmap getContactBitmapByPhotoID() {
	//		Cursor cursor  = null;
	//		try {
	//			ContentResolver contentResolver = ApplicationContext.getInstance(null).getContentResolver();
	//			cursor = contentResolver.query(
	//					Uri.withAppendedPath(Contacts.CONTENT_URI, contactId), 
	//					new String[] {Contacts.PHOTO_ID}, 
	//					null,null,null);
	//
	//			if ( cursor.moveToFirst() ) {
	//				String photoId = cursor.getString(0);
	//				cursor.close();
	//				cursor = null;
	//				if ( photoId == null ) return null; 
	//				Uri photoDataUri = Uri.withAppendedPath(Data.CONTENT_URI, photoId);
	//				cursor = contentResolver.query(photoDataUri,null,null,null,null);
	//				if ( cursor.moveToFirst() ) {
	//					byte[] photoBlob = cursor.getBlob(cursor.getColumnIndex(Contacts.Photo.DATA15));
	//					return BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
	//				}
	//				if ( cursor != null)
	//					cursor.close();
	//				return null;
	//			} else return null;
	//		} catch (Exception e) {
	//			return null;
	//		} finally {
	//			if ( cursor != null )
	//				cursor.close();
	//		}
	//	}


	private static long getPerson(SMSList smsList) {
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

	private String getNumber(SMSList smsList) {
		for(SMSItem sms : smsList) {
			String name = sms.getAddress();
			if ( name != null ) return name;
		}
		return UNKNOWN;
	}

	public String getContactNumber() {
		return this.contactNumber;
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

	public int getMessagesCount() {
		return messagesCount;
	}

	public String getLatestTime() {
		return latestTime;
	}

	public String getLatestActionMessage() {
		return latestActionMessage;
	}


	public String getContactId() {
		return contactId;
	}


	public Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt(SMSItem.THREAD_ID, threadId);
		return bundle;
	}

}
