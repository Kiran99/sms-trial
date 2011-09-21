package dtd.phs.sms.data;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

public interface IListFactory {
	BaseAdapter createAdapter(Object object);
	OnItemClickListener createOnItemClickListener(Activity act,BaseAdapter adapter);
}
