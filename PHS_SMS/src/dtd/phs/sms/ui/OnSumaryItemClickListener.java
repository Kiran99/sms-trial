package dtd.phs.sms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dtd.phs.sms.ShowConversation;
import dtd.phs.sms.data.entities.SummariesList;
import dtd.phs.sms.data.entities.SummaryItem;

public class OnSumaryItemClickListener implements OnItemClickListener {

	private Activity activity;
	private SummariesList summaries;

	public OnSumaryItemClickListener(Activity act, SummariesList summaries) {
		this.activity = act;
		this.summaries = summaries;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		SummaryItem item = summaries.get(position);
		Bundle bundle = item.getBundle();
		Intent i = new Intent(activity,ShowConversation.class);
		i.putExtra( ShowConversation.INPUT_BUNDLE, bundle);		
		activity.startActivity(i);
	}

}
