package dtd.phs.sms.message_center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class ConnectivityChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);		
		boolean connected = (   
				conMgr.getActiveNetworkInfo() != null &&
	            conMgr.getActiveNetworkInfo().isAvailable() &&
	            conMgr.getActiveNetworkInfo().isConnected()   );
		Intent i = new Intent(context,GoogleXMPPService.class);
		GoogleXMPPService.messageToSend = null;
		if ( connected ) {
			context.startService(i);
		} else {
			context.stopService(i);
		}
		

	}

}
