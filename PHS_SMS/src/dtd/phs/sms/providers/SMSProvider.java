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
		Uri smsURI = Uri.parse(SMS_PROVIDER);
		Context context = ApplicationContext.getInstance(null);
		
		Cursor cursor = context.getContentResolver().query(
				smsURI,
				null,
				null, null, sortField);
		SMSList list = new SMSList();
		if ( cursor.moveToFirst())  {
			while (true) {
				SMSItem item = new SMSItem( cursor );
				list.add(item);
				if ( ! cursor.moveToNext()) break;
			}
		}
		cursor.close();
		return list;
	} 
}
