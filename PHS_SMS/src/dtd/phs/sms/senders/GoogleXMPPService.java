package dtd.phs.sms.senders;

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
import dtd.phs.sms.test.XMPP_Activity;
import dtd.phs.sms.util.Logger;
import dtd.phs.sms.util.PreferenceHelpers;

public class GoogleXMPPService extends Service {

	public static final String EXTRA_SENDER = "i_sms_receiver";
	public static final String EXTRA_MESSAGE_BODY = "i_sms_body";
	public static final String EXTRA_TIME_STAMP = "time_stamp";

	private static final String HOST = "talk.google.com";
	private static final String PORT = "5222";
	private static final String SERVICE = "googlemail.com";
	private static final String SEPERATOR = " xtuoioutx ";
	private static final String PONG_MESSAGE = "6fdb087aa3fbfbcb"; //pong[0..15]
	private static final String PING_MESSAGE = "df911f0151f9ef02"; //ping[0..15]
	
	public static final String I_MESSAGE_DELIVERED = "dtd.phs.sms.isms_delivered";
	public static final String I_MESSAGE_RECEIVED = "dtd.phs.sms.isms_received";
	public static final String XMPP_FAILURE = "dtd.phs.sms.xmpp_failure";

	public  static final int CONNECTION_ERROR = -1;
	public static final int AUTHENTICATION_ERROR = -2;
	public static final int I_MESSAGE_TIME_OUT = -3;
	public static final int UNKNOWN_ERROR = -99;

	public static final String ERROR_CODE = "xmpp.error_code";
	



	public static MessageItem messageToSend;
	private XMPPConnection connection;
	public HashMap<String, String> waitingMessages;
	Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
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
						if ( isDeliveryStatusMessage(messageBody)) {
							processReplyMessage(fromName, messageBody);
						} else {
							processChatMessage(fromName, messageBody);
						}
					}
				}

				//Note: every message (include sent messages) must be send with the following format:
				//[timestamp][SEPERATOR][content]
				private void processChatMessage(String fromName, String messageBody) { //1. reply the sender: [timestamp(sender)][SEPERATOR][hash[PONG][0..15]]
					//2. broadcast "receive a message" with format ["(["sender"])"][message_content]
					sendMessage(fromName,createPongMessage(messageBody));
					broadcastMessageReceived(fromName,messageBody);
				}

				private void sendMessage(String fromName,String pongMessage) {
					if ( pongMessage != null) {
						Message msg = new Message(fromName, Message.Type.chat);
						msg.setBody(pongMessage);
						//TODO: try catch
						connection.sendPacket(msg);
					} else {
						Logger.logInfo("Pong message is NULL");
					}
				}

				private String createPongMessage(String messageBody) {
					String[] words = messageBody.split(SEPERATOR);
					if (words.length == 2 ) {
						String timeStamp = words[0];
						return timeStamp+SEPERATOR+PONG_MESSAGE;
					} else {
						Logger.logInfo("Received a weird message");
						return null;
					}
				}

				private void processReplyMessage(String fromName, String messageBody) {
					Logger.logInfo("PONG MESSAGE RECEIVED !");
					String[] words = messageBody.trim().split(SEPERATOR);
					String timeStamp = words[0];
					synchronized ( waitingMessages ) {
						Logger.logInfo("PONG MESSAGE RECEIVED - TIME: " + timeStamp);
						if (waitingMessages.containsKey(timeStamp)) {
							Logger.logInfo("Remove the message with time stamp: " + timeStamp);
							waitingMessages.remove(timeStamp);
							broadcastDeliveredIntent(timeStamp);
						}
					}
				}

				private void broadcastDeliveredIntent(String timeStamp) {
					Intent intent = new Intent();
					intent.setAction(I_MESSAGE_DELIVERED);
					intent.putExtra(EXTRA_TIME_STAMP, timeStamp);
					getApplicationContext().sendBroadcast(intent);
				}

				private boolean isDeliveryStatusMessage(String messageBody) {
					String[] words = messageBody.split(SEPERATOR);
					if (words.length == 2 && words[1].equals(PONG_MESSAGE)) 
						return true;
					return false;
				}
			}, filter);
		}

	}

	protected void broadcastMessageReceived(String fromName, String messageBody) {
		Intent intent = new Intent();
		intent.setAction(I_MESSAGE_RECEIVED);
		intent.putExtra(EXTRA_SENDER,fromName);
		intent.putExtra(EXTRA_MESSAGE_BODY, messageBody);
		getApplicationContext().sendBroadcast(intent);		
	}

	private void login() {
		Logger.logInfo("Creating a connection ...");
		ConnectionConfiguration connConfig =
			new ConnectionConfiguration(HOST, Integer.parseInt(PORT), SERVICE);
		connection = new XMPPConnection(connConfig);

		boolean connected = false;
		try {
			connection.connect();
			connected = true;
		} catch (XMPPException exception) {			
			Logger.logException(exception);
			broadcastFailure(CONNECTION_ERROR);
		}

		if ( connected ) {
			try {
				connection.login(getUserName(), getPassword() );
				Logger.logInfo("Connection is created successfully !");
			} catch (XMPPException exception) {
				Logger.logException(exception);
				broadcastFailure(AUTHENTICATION_ERROR);
			}
		}
	}

	private void broadcastFailure(int errorCode) {
		Intent intent = new Intent();
		intent.setAction(XMPP_FAILURE);
		intent.putExtra(ERROR_CODE,errorCode);
	}

	private String getPassword() {
		//TODO: later
//		return XMPP_Activity.ME_GMAIL_PWD;
		return PreferenceHelpers.getPassword(getApplicationContext());
	}

	private String getUserName() {
		//TODO: later
//		return XMPP_Activity.ME_GMAIL_ACC;
		return PreferenceHelpers.getUsername(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ( messageToSend != null ) {
			Message msg = new Message(messageToSend.getNumber(), Message.Type.chat);
			long currentTime = System.currentTimeMillis();
			msg.setBody(""+ currentTime + SEPERATOR + messageToSend.getContent());
			waitingMessages.put(""+currentTime, "");
			//TODO: try catch
			try {
				connection.sendPacket(msg);
			} catch (Exception e) {
				Logger.logException(e);
			}
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
				synchronized (waitingMessages) {
					waitingMessages.wait(WAITING_FOR_REPLY_TIME);
					if ( waitingMessages.containsKey(startTime)) {
						Logger.logInfo("Message with timestamp : [" + startTime + " ] is still waiting");
						timeOut(startTime); //remove message & failed !
					} else {
						Logger.logInfo("No message with timestamp : [" + startTime + " ] is waiting anymore ");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

	public void timeOut(String startTime) {
		
		Logger.logInfo("Time out !");
		waitingMessages.remove(startTime);
		Intent intent = new Intent();
		intent.setAction(XMPP_FAILURE);
		intent.putExtra(ERROR_CODE, I_MESSAGE_TIME_OUT);
		intent.putExtra(EXTRA_TIME_STAMP, startTime);
		getApplicationContext().sendBroadcast(intent);

	}

}
