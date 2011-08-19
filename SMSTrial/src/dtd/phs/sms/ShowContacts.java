package dtd.phs.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowContacts extends Activity {

	private static final String SEP_NAME_NUMBER = "---";
	private ListView contactsList;
	private ContentResolver cr;
	private ArrayList<String> data;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list);
		contactsList = (ListView) findViewById(R.id.contacts_list);

		getContacts();
	}

	private void getContacts() {
		cr = getContentResolver();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Cursor cursor = managedQuery(uri, null, null, null, null);
		data = new ArrayList<String>();
		if ( cursor.moveToFirst() ) {
			while (true) {
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String numbers = getNumbers( cursor);
				data.add( name + SEP_NAME_NUMBER + numbers);
				if ( ! cursor.moveToNext() ) break;
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data);
		contactsList.setAdapter(adapter);
	}

	private String getNumbers(Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		String result = "";
		if (Integer.parseInt(cursor.getString(
				cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
			Cursor pCur = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
							new String[]{id}, null);
			while (pCur.moveToNext()) {
				result += pCur.getString(pCur.getColumnIndex(Phone.NUMBER))+"::";

			} 
			pCur.close();
		}
		return result;

	}

}
