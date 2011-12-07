package dtd.phs.sms.data;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

public interface IListFactory {
	BaseAdapter createAdapter(Object object, Context context);
	OnItemClickListener createOnItemClickListener(Activity act,BaseAdapter adapter);
}
