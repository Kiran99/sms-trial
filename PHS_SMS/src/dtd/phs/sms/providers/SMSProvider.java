package dtd.phs.sms.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.global.ApplicationContext;

public class SMSProvider {
	public static final String SMS_PROVIDER = "content://sms/"; 
	public static final String SMS_INBOX = "content://sms/inbox/"; 
	public static final String SMS_SENT = "content://sms/sent/";

	public static SMSList getAllMessagesSortBy(String sortField) {
		return getMessages(null, sortField);
	}

	private static SMSList getMessages(String cond,String sortField) {
		Uri smsURI = Uri.parse(SMS_PROVIDER);
		Context context = ApplicationContext.getInstance(null);
		SMSList list = new SMSList();
		Cursor cursor = null;
		try {
			cursor = 
				context.getContentResolver().
				query(smsURI,null,cond,null, sortField);

			if ( cursor.moveToFirst())  {
				while (true) {
					SMSItem item = new SMSItem( cursor );
					list.add(item);
					if ( ! cursor.moveToNext()) break;
				}
			}
			return list;
		} catch (Exception e) {
			return list;
		} finally {
			if ( cursor != null )
				cursor.close();
		}
	}

	public static SMSList getMessagesForThread(int threadId) {
		String sortField = SMSItem.DATE + " asc";
		String cond = SMSItem.THREAD_ID + " = " + threadId;
		return getMessages( cond, sortField);
	} 
}
