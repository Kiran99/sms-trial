package dtd.phs.sms.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dtd.phs.sms.global.ApplicationContext;

public class Helpers {

	public static View inflate(int layout, ViewGroup root) {
		Context context = ApplicationContext.getInstance(null);
		LayoutInflater i = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return i.inflate(layout, root);
	}

}
