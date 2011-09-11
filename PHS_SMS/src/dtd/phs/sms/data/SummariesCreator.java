package dtd.phs.sms.data;

import java.util.HashMap;

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
		Summ
	}

}
