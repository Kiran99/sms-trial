package dtd.phs.sms.data;

import android.content.Context;
import dtd.phs.sms.data.DataWrapper.Type;
import dtd.phs.sms.data.entities.SMSItem;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.data.entities.SummariesList;
import dtd.phs.sms.global.UIThreadHandler;
import dtd.phs.sms.providers.SMSProvider;
import dtd.phs.sms.util.Logger;

public class GetSummariesRequest implements Request {

	private IDataGetter listener;
	private Context context;

	public GetSummariesRequest(IDataGetter dataGetter,Context context) {
		this.listener = dataGetter;
		this.context = context;
	}

	@Override
	public void run() {
		
		long start = System.currentTimeMillis();
		SMSList allMessages = SMSProvider.getAllMessagesSortBy(SMSItem.THREAD_ID+" asc", context);
		
		Logger.logInfo("Time loading all messages: " + (System.currentTimeMillis()- start));
		start = System.currentTimeMillis();
		
		SummariesList summaries = SummariesCreator.createSummaries(allMessages, context);
		Logger.logInfo("Time creating summaries: " + (System.currentTimeMillis()- start));

		final DataWrapper wrapper = new DataWrapper(Type.SUMMARIES_LIST,summaries);
		UIThreadHandler.getInstance().post(new Runnable() {
			@Override
			public void run() {
				listener.onGetDataSuccess(wrapper);
			}
		});
	}

}
