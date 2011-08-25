package dtd.phs.sms.data.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.net.Uri;

public class SummariesList extends ArrayList<SummaryItem> {
	private static final long serialVersionUID = -4053587841265625274L;

	public Uri getAvatarURI(int position) {
		return this.get(position).getAvatarURI();
	}
	
	public Bitmap getAvatarBitmap(int position) {
		return this.get(position).getContactPhoto();
	}

	public String getContactName(int position) {
		return this.get(position).getContactName();	
	}

	public int getMessagesCount(int position) {
		return this.get(position).getMessagesCount();
	}

	public String getLatestTime(int position) {
		return this.get(position).getLatestTime();
	}

	/**
	 * @return latest message OR draft
	 */
	public String getLatestActionMessage(int position) {
		return this.get(position).getLatestActionMessage();	
	}
	
	

	/**
	 * @param messages 
	 * 		precondition: messages must be sorted by ThreadId
	 * @return list of message summaries
	 */
	public static SummariesList createFrom(SMSList messages) {
		
		
		HashMap<Integer, SMSList> mapThreadId2MessageList = groupByThread(messages);
		
		return createFrom(mapThreadId2MessageList);
	}

	private static HashMap<Integer, SMSList> groupByThread(SMSList messages) {
		//TODO: is there thread id null ??
		HashMap<Integer, SMSList> mapThreadId2MessageList = new HashMap<Integer, SMSList>();
		for(SMSItem sms : messages) {
			Integer threadId = new Integer(sms.getThreadId());
		
			SMSList list = mapThreadId2MessageList.get(threadId);
			if ( list == null) {
				list = new SMSList();
				mapThreadId2MessageList.put(threadId,list);				 
			}
			
			list.add(sms);
		}
		return mapThreadId2MessageList;
	}

	private static SummariesList createFrom(HashMap<Integer, SMSList> mapThreadId2MessageList) {
		SummariesList summaries = new SummariesList();
		for(Integer threadId : mapThreadId2MessageList.keySet()) {
			SMSList smsList = mapThreadId2MessageList.get(threadId);
			SummaryItem summItem = new SummaryItem(smsList);
			summaries.add(summItem);			
		}
		SummariesList.sortByTime(summaries);
		return summaries;
	}

	private static void sortByTime(SummariesList summaries) {
		Collections.sort(summaries, new SummaryItem.TimeComparator());
	}

}
