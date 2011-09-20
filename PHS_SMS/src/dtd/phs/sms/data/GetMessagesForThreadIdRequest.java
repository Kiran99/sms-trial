package dtd.phs.sms.data;

import dtd.phs.sms.data.DataWrapper.Type;
import dtd.phs.sms.data.entities.SMSList;
import dtd.phs.sms.global.UIThreadHandler;
import dtd.phs.sms.providers.SMSProvider;

public class GetMessagesForThreadIdRequest implements Request {

	private int threadId;
	private IDataGetter delegate;

	public GetMessagesForThreadIdRequest(int threadId, IDataGetter delegate) {
		this.threadId = threadId;
		this.delegate = delegate;
	}

	@Override
	public void run() {
		final SMSList messages = SMSProvider.getMessagesForThread(threadId);
		UIThreadHandler.getInstance().post(new Runnable() {
			@Override
			public void run() {
				delegate.onGetDataSuccess(new DataWrapper(Type.SMS_LIST, messages));
			}
		});
	}

}
