package dtd.phs.sms.senders;

import java.util.Currency;
import java.util.HashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import dtd.phs.sms.data.entities.MessageItem;
import dtd.phs.sms.util.Logger;

public class GoogleXMPPService extends Service {



	private static final String HOST = "talk.google.com";
	private static final String PORT = "5222";
	private static final String SERVICE = "googlemail.com";
	private static final String SEPERATOR = "xxxoioxxx";
	public static final String TIME_OUT_I_MESSAGE = "dtd.phs.sms.time_out";
	public static final String TIME_STAMP = "time_stamp";
	protected static final String I_MESSAGE_DELIVERED = "dtd.phs.sms.isms_delivered";
	public static MessageItem messageToSend;
	private XMPPConnection connection;
	public HashMap<String, String> waitingMessages;
	Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		waitingMessages = new HashMap<String, String>();
		createConnection();
	}

	@Override
	public void onDestroy() {
		try {
			if ( connection != null )
				connection.disconnect();
		} catch (Exception e) {
			Logger.logException(e);
		}

		super.onDestroy();
	}

	private void createConnection() {
		login();
		if ( connection != null ) {
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message incomingMessage = (Message) packet;
					if (incomingMessage.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(incomingMessage.getFrom());
						String messageBody = incomingMessage.getBody();
						//						TODO: qadwedsad --- what to do with reply message ?
						if ( isReplyMessage(messageBody)) {
							processReplyMessage(fromName, messageBody);
						} else {
							processChatMessage(fromName, messageBody);
						}
					}
				}

				private void processReplyMessage(String fromName, String messageBody) {
					String[] words = messageBody.trim().split(SEPERATOR);
					String timeStamp = words[0];
					if (waitingMessages.containsKey(timeStamp)) {
						waitingMessages.remove(timeStamp);
					}
					broadcastDeliveredIntent();
				}

				private void broadcastDeliveredIntent() {
					Intent intent = new Intent();
					intent.setAction(I_MESSAGE_DELIVERED);
					getApplicationContext().sendBroadcast(intent);
				}

				private boolean isReplyMessage(String messageBody) {
					// TODO Auto-generated method stub
					return false;
				}
			}, filter);
		}

	}

	private void login() {
		Logger.logInfo("Create a connection !");
		ConnectionConfiguration connConfig =
			new ConnectionConfiguration(HOST, Integer.parseInt(PORT), SERVICE);
		connection = new XMPPConnection(connConfig);
		try {
			connection.connect();
		} catch (XMPPException exception) {
			//TODO: listener
			Logger.logException(exception);
		}

		try {
			connection.login(getUserName(), getPassword() );
			Logger.logInfo("Connection is created successfully !");
		} catch (XMPPException exception) {
			//TODO: listener
			Logger.logException(exception);
		}
	}

	private String getPassword() {
		return "itrangdethuong";
	}

	private String getUserName() {
		return "sphamhung";
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ( messageToSend != null ) {
			Message msg = new Message(messageToSend.getNumber(), Message.Type.chat);
			long currentTime = System.currentTimeMillis();
			msg.setBody(""+ currentTime + SEPERATOR + messageToSend.getContent());
			connection.sendPacket(msg);
			WaitingThread wt = new WaitingThread(currentTime);
			wt.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public class WaitingThread extends Thread {
		private static final long WAITING_FOR_REPLY_TIME = 3000;
		private String startTime;

		public WaitingThread(long currentTime) {
			this.startTime = ""+currentTime;
		}

		@Override
		public void run() {
			try {
				currentThread().wait(WAITING_FOR_REPLY_TIME);
				if ( waitingMessages.containsKey(startTime)) {
					timeOut(startTime); //remove message & failed !
				} else {
					//already removed -> got reply already
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void timeOut(String startTime) {
		waitingMessages.remove(startTime);
		Intent intent = new Intent();
		intent.setAction(TIME_OUT_I_MESSAGE);
		intent.putExtra(TIME_STAMP, startTime);
		getApplicationContext().sendBroadcast(intent);

	}

}
