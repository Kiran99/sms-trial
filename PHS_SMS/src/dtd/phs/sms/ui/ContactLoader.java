package dtd.phs.sms.ui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.ImageView;
import android.widget.TextView;
import dtd.phs.sms.global.ApplicationContext;
import dtd.phs.sms.global.UIThreadHandler;
import dtd.phs.sms.util.Logger;

public class ContactLoader implements Runnable {

	TextViewStack stack = new TextViewStack();
	private HashMap<String, String> cache;
	private HashMap<String, Bitmap> cacheAvatar;

	public ContactLoader() {
		loadContactThread = new Thread(this);
		cache = new HashMap<String, String>();
		cacheAvatar = new HashMap<String, Bitmap>();
		loadContactThread.setPriority(Thread.NORM_PRIORITY -1);
		loadContactThread.start();
	}

	public void loadContact(TextView tvContact , ImageView ivAvatar, String contactId) {
		TextViewToLoad tvLoad = new TextViewToLoad(tvContact, ivAvatar, contactId);
		stack.add(tvLoad);

	}

	public void stopThread() {
		cache.clear();
		cacheAvatar.clear(); 
		loadContactThread.interrupt();
	}

	@Override
	public void run() {
		while (true) {
			try {
				final TextViewToLoad tvLoad = stack.pop();
				if ( tvLoad.contactId == null ) continue;
				final String contactName = getContactName(tvLoad.contactId);
				final Bitmap avatar = getContactPhoto(tvLoad.contactId);
				UIThreadHandler.getInstance().post(new Runnable() {
					@Override
					public void run() {
						if (contactName != null )
							tvLoad.tvToLoad.setText(contactName);
						if ( avatar != null )
							tvLoad.ivToLoad.setImageBitmap(avatar);
					}
				});

				if (Thread.interrupted())
					throw new InterruptedException();
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private String getContactName(String contactId) {
		if ( contactId == null ) return null;

		String contactName = cache.get(contactId);
		if ( contactName != null ) return contactName;

		Cursor cursor = null;
		try {
			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
			cursor = 
				ApplicationContext.getInstance(null).getContentResolver().
				query(contactUri, new String[] {Contacts.DISPLAY_NAME}, null, null, null);
			if ( cursor.moveToFirst() ) {
				contactName = cursor.getString(0);
				cache.put(contactId, contactName);
				return contactName; //+ " ::ID = " + contactId;
			} else return null;
		} catch (Exception e) {
			Logger.logException(e);
			return null;
		} finally {
			cursor.close();
		}
	}


	public InputStream getContactPhotoStream(String contactId) {
		Context context = ApplicationContext.getInstance(null);
		Uri contactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,contactId);
		try {
			return ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactURI);
		} catch (Exception e) {
			Logger.logException(e);
			return null;
		}
	}
	public Bitmap getContactPhoto(String contactId) {
		if (SummariesAdapter.STUB_AVATAR_BITMAP == null) 
			SummariesAdapter.STUB_AVATAR_BITMAP = BitmapFactory.decodeResource(ApplicationContext.getInstance(null).getResources(), SummariesAdapter.STUB_AVATAR);
		if ( contactId == null ) return  SummariesAdapter.STUB_AVATAR_BITMAP;

		Bitmap avatar = cacheAvatar.get(contactId);
		if ( avatar == null ) {
			avatar = getJustPhoto(contactId);
			cacheAvatar.put(contactId, avatar);
		}
		return avatar;
	}

	private Bitmap getJustPhoto(String contactId) {
//		Bitmap avatar = null;
//		InputStream is = getContactPhotoStream(contactId);
//		if (is == null) {
//			avatar = SummariesAdapter.STUB_AVATAR_BITMAP;
//		} else {
//			avatar = BitmapFactory.decodeStream(is);
//		}
//		return avatar;
		
		Bitmap avatar = SummariesAdapter.STUB_AVATAR_BITMAP;
		String[] projection = {ContactsContract.CommonDataKinds.Photo.PHOTO};
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String where = ContactsContract.Data.MIMETYPE 
		       + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "' AND " 
		       + ContactsContract.Data.CONTACT_ID + " = " + contactId;
		Context context = ApplicationContext.getInstance(null);
		Cursor cursor = context.getContentResolver().query(uri, projection, where, null, null);
		if( cursor != null && cursor.moveToFirst() ) {
		        byte[] photoData = cursor.getBlob(0);
		        if ( photoData != null )
		        	avatar = BitmapFactory.decodeByteArray(photoData, 0,photoData.length, null);
		}  
		cursor.close();
		return avatar;
		
	}

	private Bitmap getBitMapByBlahMethod(String contactId) {
		// TODO Auto-generated method stub
		return null;
	}

	public class TextViewToLoad {
		public TextView tvToLoad;
		public ImageView ivToLoad;
		public String contactId;
		public TextViewToLoad(TextView tvToLoad, ImageView ivAvatar, String contactId) {
			this.tvToLoad = tvToLoad;
			this.contactId = contactId;
			this.ivToLoad = ivAvatar;
		}

	}

	private Thread loadContactThread;
	class TextViewStack {
		private Stack<TextViewToLoad> stack = new Stack<TextViewToLoad>();
		public void add(TextViewToLoad tvLoad) {
			synchronized (this) {
				clean(tvLoad);
				stack.addElement(tvLoad);			
				this.notify();
			}
		}

		private void clean(TextViewToLoad tvLoad) {
			for(int i = 0; i < stack.size() ;) {
				TextViewToLoad tv = stack.get(i);
				if (tv.tvToLoad == tvLoad.tvToLoad) {
					stack.remove(i);
				} else i++;
			}
		}

		public TextViewToLoad pop() throws InterruptedException {
			synchronized (this) {
				if ( stack.isEmpty() )
					this.wait();
			}
			return stack.pop(); 
		}

	}

}
