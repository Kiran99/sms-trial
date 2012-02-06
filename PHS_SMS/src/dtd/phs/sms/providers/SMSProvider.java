package dtd.phs.sms.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;

public class SMSProvider {
	public static final String SMS_PROVIDER = "content://sms/"; 
	public static final String SMS_INBOX = "content://sms/inbox/"; 
	public static final String SMS_SENT = "content://sms/sent/";

	public static SMSList getAllMessagesSortBy(String sortField,Context context) {
		return getMessages(null, sortField, context);
	}

	private static SMSList getMessages(String cond,String sortField,Context context) {
		Uri smsURI = Uri.parse(SMS_PROVIDER);
		SMSList list = new SMSList();
		Cursor cursor = null;
		try {
			cursor = 
				context.getContentResolver().
				query(smsURI,null,cond,null, sortField);

			if ( cursor.moveToFirst())  {
				while (true) {
					SMSItem item = new SMSItem( cursor );
//					printOut(cursor);
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

//	private static void printOut(Cursor cursor) {
//		String[] columnNames = cursor.getColumnNames();
//		StringBuilder message = new StringBuilder();
//		for(String col : columnNames) {
//			String s = cursor.getString(cursor.getColumnIndex(col));
//			message.append("["+ col +": "+ s+"]");
//		}
//	}

	public static SMSList getMessagesForThread(int threadId, Context context) {
		String sortField = SMSItem.DATE + " asc";
		String cond = SMSItem.THREAD_ID + " = " + threadId;
		return getMessages( cond, sortField, context);
	} 
}
