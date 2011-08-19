package dtd.phs.sms;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowInbox extends Activity {
	private ListView messageThreads;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list);
		messageThreads = (ListView) findViewById(R.id.contacts_list);
		getMessages();
	}

	private void getMessages() {
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, "date desc");
		startManagingCursor(cursor1);
		String[] columns = new String[] { "address", "person", "date", "body","type","thread_id" };
		
		ArrayList<String> arr = new ArrayList<String>();
		if (cursor1.getCount() > 0) {
			String count = Integer.toString(cursor1.getCount());
			Log.e("Count",count);
			while (cursor1.moveToNext()) {
				
				String address = cursor1.getString(cursor1
						.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1
						.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1
						.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1
						.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1
						.getColumnIndex(columns[4]));
				String threadId = cursor1.getString(cursor1.getColumnIndex(columns[5]));
				
				date = getReadableDate( date );
//				Log.i("phs", "phs:: " + "a: " + address + " -- n: " + name + " -- m: " + msg + " --t_id: " + threadId);
				String data = address+"::"+date+"::"+msg;
				arr.add( data );
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arr);
		messageThreads.setAdapter(adapter);
	}

	private String getReadableDate(String dateStr) {
		long milis = 0;
		try {
			milis = Long.parseLong(dateStr);
		} catch (Exception e) {}
		
		Date date = new Date( milis );
		return date.toGMTString();
	}


} 