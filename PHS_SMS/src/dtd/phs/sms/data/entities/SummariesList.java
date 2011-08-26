package dtd.phs.sms.data.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import android.net.Uri;
import dtd.phs.sms.util.Logger;

public class SummariesList extends ArrayList<SummaryItem> {
	private static final long serialVersionUID = -4053587841265625274L;
	private static final String NO_CONTACT = "NULL";

	public Uri getAvatarURI(int position) {
		return this.get(position).getAvatarURI();
	}

	//	public Bitmap getAvatarBitmap(int position) {
	//		return this.get(position).getContactPhoto();
	//	}

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
		ArrayList<SMSList> groups = groupByContactId(mapThreadId2MessageList);
		mapThreadId2MessageList.clear();
		return createFrom(groups);
	}

	private static ArrayList<SMSList> groupByContactId(HashMap<Integer, SMSList> mapThreadId2MessageList) {
			//fuck
		/**
		 * 1. Find out contactId for each MessageGroup (grouped already by threadId)
		 * 2. 
		 * 	a.If contactId == no_contact:
		 * 	- get all messages, and create a new group
		 * 	b.If contactId exists:
		 * 	- find all thread_group with the same contactId
		 * 	- group them into new group
		 */
		HashMap<Integer, String> mapThreadId2ContactId = new HashMap<Integer, String>();
		ArrayList<SMSList> groups = new ArrayList<SMSList>();
		for(Integer threadId : mapThreadId2MessageList.keySet()) {
			String contactID = SummaryItem.getContactID(mapThreadId2MessageList.get(threadId));
			if ( contactID == null ) {
				groups.add(mapThreadId2MessageList.get(threadId));
			} else mapThreadId2ContactId.put(threadId,contactID);
		}
		Collection<String> values = new HashSet<String>(mapThreadId2ContactId.values());
		for(String contactId : values) {
			SMSList list = new SMSList();
			for(Integer threadId : mapThreadId2MessageList.keySet()) {
				String tmpContactId = mapThreadId2ContactId.get(threadId);
				if ( tmpContactId != null && tmpContactId.equals(contactId) ) {
					list.addAll(mapThreadId2MessageList.get(threadId));
				}
			}
			groups.add(list);
		}
		
		return groups;
		
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

	private static SummariesList createFrom(ArrayList<SMSList> groups) {
		SummariesList summaries = new SummariesList();
		for(SMSList smsList : groups) {
			SummaryItem summItem = new SummaryItem(smsList);
			summaries.add(summItem);			
		}
		SummariesList.sortByTime(summaries);
		return summaries;
	}

	private static void sortByTime(SummariesList summaries) {
		Logger.logInfo(" summaries is sort !");
		Collections.sort(summaries, new SummaryItem.TimeComparator());
	}

	public String getContactId(int position) {
		return this.get(position).getContactId();
	}

	public String getContactNumber(int position) {
		return this.get(position).getContactNumber();	
	}

}
