package dtd.phs.sms.data;

import java.util.HashMap;
import java.util.Map;

import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.data.entities.SummariesList;
import dtd.phs.sms.data.entities.SummaryItem;

public class SummariesCreator {

	/**
	 * 
	 * @param allMessages precondition: the messages are sorted by threadId
	 * @return
	 */
	public static SummariesList createSummaries(SMSList allMessages) {
		SummariesList summList = new SummariesList();
		HashMap<Integer, SMSList> mapThreadId2Messages = divideToGroups(allMessages);
		for(Integer threadId : mapThreadId2Messages.keySet()) {
			SMSList list = mapThreadId2Messages.get(threadId);
			SummaryItem item = new SummaryItem(list);
			summList.add(item);
		}
		SummariesList.sortByTime(summList);
		return summList;
	}

	private static HashMap<Integer, SMSList> divideToGroups(SMSList allMessages) {
		HashMap<Integer, SMSList> map = new HashMap<Integer, SMSList>();
		for(int i = 0 ; i < allMessages.size() ; i++) {
			SMSList list = new SMSList();
			SMSItem firstItem = allMessages.get(i);
			for(int j = i; j < allMessages.size() ; j++) {
				SMSItem item = allMessages.get(j);
				if ( item.getThreadId() != firstItem.getThreadId() ) break;
				list.add(item);
				i = j;
			}
			map.put(firstItem.getThreadId(), list);			
				
		}
		return map;
	}

}
