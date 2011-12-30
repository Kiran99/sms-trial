package dtd.phs.sms.message_center;

import java.io.ObjectOutputStream.PutField;
import java.util.HashMap;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import dtd.phs.sms.data.entities.MessageItem;
import dtd.phs.sms.util.Helpers;
import dtd.phs.sms.util.Logger;
import dtd.phs.sms.util.PreferenceHelpers;

public class GoogleXMPPService extends Service {

	public static final String EXTRA_SENDER = "i_sms_receiver";
	public static final String EXTRA_MESSAGE_BODY = "i_sms_body";
	static final String EXTRA_MESSAGE_ID = "i_message_id";

	private static final String HOST = "talk.google.com";
	private static final String PORT = "5222";
	public static final String SERVICE = "gmail.com";
	private static final String SEPERATOR = " xtuoioutx ";
	private static final String PONG_MESSAGE = "6fdb087aa3fbfbcb"; //pong[0..15]
	private static final String PING_MESSAGE = "df911f0151f9ef02"; //ping[0..15]

	public static final String I_MESSAGE_DELIVERED = "dtd.phs.sms.isms_delivered";
	public static final String I_MESSAGE_RECEIVED = "dtd.phs.sms.isms_received";
	public static final String XMPP_FAILURE = "dtd.phs.sms.xmpp_failure";

	public  static final int CREATE_CONNECTION_ERROR = -1;
	public static final int AUTHENTICATION_ERROR = -2;
	public static final int I_MESSAGE_TIME_OUT = -3;
	public static final int NOT_CONNECTED_TO_SERVER = -4;
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
		Logger.logInfo("GoogleXMPP service is created !");
		new Thread(new Runnable() {
			@Override
			public void run() {
				createConnection();			
			}
		}).start();

	}

	@Override
	public void onDestroy() {
		try {
			if ( connection != null ) {
				Logger.logInfo("Connection is disconnected !");
				connection.disconnect();
			}
		} catch (Exception e) {
			Logger.logException(e);
		}

		super.onDestroy();
	}

	private void createConnection() {
		connection = null;
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
				private void processChatMessage(String fromName, String messageBody) { //1. reply the sender: [(sender)][SEPERATOR][hash[PONG][0..15]]
					//2. broadcast "receive a message" with format ["(["sender"])"][message_content]
					sendMessage(fromName,createPongMessage(messageBody));
					broadcastMessageReceived(fromName,messageBody);
				}

				private String createPongMessage(String messageBody) {
					String[] words = messageBody.split(SEPERATOR);
					if (words.length == 2 ) {
						String id = words[0];
						return id+SEPERATOR+PONG_MESSAGE;
					} else {
						Logger.logInfo("Received a weird message");
						return null;
					}
				}

				private void processReplyMessage(String fromName, String messageBody) {
					Logger.logInfo("PONG MESSAGE RECEIVED !");
					String[] words = messageBody.trim().split(SEPERATOR);
					String id = words[0];
					synchronized ( waitingMessages ) {
						Logger.logInfo("PONG MESSAGE RECEIVED - ID:--" + id+"--");
						if (waitingMessages.containsKey(id)) {
							Logger.logInfo("Remove the message with ID:--" + id+"--");
							waitingMessages.remove(id);
							broadcastDeliveredIntent(id);
						}
					}
				}

				private void broadcastDeliveredIntent(String id) {
					Intent intent = new Intent();
					intent.setAction(I_MESSAGE_DELIVERED);
					intent.putExtra(EXTRA_MESSAGE_ID, id);
					getApplicationContext().sendBroadcast(intent);
				}

				private boolean isDeliveryStatusMessage(String messageBody) {
					Logger.logInfo("Inside isDelivery - meesage: " + messageBody);
					String[] words = messageBody.split(SEPERATOR);
					if (words.length == 2 && words[1].equals(PONG_MESSAGE)) 
						return true;
					return false;
				}
			}, filter);
		}

	}


	private void sendMessage(final String toName,final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if ( message != null) {
					Logger.logInfo("Message sending: to = [" + toName + "] -- content: " + message);
					Message msg = new Message(toName, Message.Type.chat);
					msg.setBody(message);
					try {
						connection.sendPacket(msg);
					} catch (Exception e ) {
						Logger.logException(e);
					}
				} else {
					Logger.logInfo("Message to send is NULL");
				}
			}
		}).start();
	}


	protected void broadcastMessageReceived(String fromName, String messageBody) {
		Intent intent = new Intent(); 
		intent.setAction(I_MESSAGE_RECEIVED);
		intent.putExtra(EXTRA_SENDER,fromName);
		Bundle bundle = extractMessage( messageBody );
		if ( bundle != null ) {
			intent.putExtra(EXTRA_MESSAGE_BODY, bundle.getString("body"));
			intent.putExtra(EXTRA_MESSAGE_ID, bundle.getString("id"));

			getApplicationContext().sendBroadcast(intent);
		}
	}

	private Bundle extractMessage(String messageBody) {
		String[] words = messageBody.split(SEPERATOR);
		Bundle bundle = new Bundle();
		if ( words.length != 2 ) {
			Logger.logInfo("Message: " + messageBody + " doesnt follow the format !");
			return null;
		} else {			
			bundle.putString("id",words[0]);
			bundle.putString("body",words[1]);
			return bundle;
		}
	}

	private void login() {
		Logger.logInfo("Creating a connection ...");
		ConnectionConfiguration connConfig =
			new ConnectionConfiguration(HOST, Integer.parseInt(PORT), SERVICE);
		
		connection = new XMPPConnection(connConfig);

		boolean connected = false;
		if ( Helpers.isConnectedToInternet(getApplicationContext())) {
			try {
				connection.connect();
				connection.addPacketSendingListener(new PacketListener() {
					
					@Override
					public void processPacket(Packet arg0) {
						// TODO Auto-generated method stub
						
					}
				}, new PacketFilter() {
					
					@Override
					public boolean accept(Packet arg0) {
						// TODO Auto-generated method stub
						return false;
					}
				});
				
				connection.addConnectionListener(new ConnectionListener() {
					//TODO: should only have 1 listener ! Dont create them every time new connection is started ! 
					
					@Override
					public void reconnectionSuccessful() {
						Logger.logInfo("Reconnecting successful !");						
					}
					
					@Override
					public void reconnectionFailed(Exception arg0) {
						Logger.logInfo("Reconnecting failed !");
					}
					
					@Override
					public void reconnectingIn(int arg0) {
						Logger.logInfo("Reconnecting In !");
					}
					
					@Override
					public void connectionClosedOnError(Exception arg0) {
						Logger.logInfo("Connection closed or ERROR !");
					}
					
					@Override
					public void connectionClosed() {
						Logger.logInfo("Connection closed !");
					}
				});
				XMPPConnection.addConnectionCreationListener(new ConnectionCreationListener() {
					
					@Override
					public void connectionCreated(Connection arg0) {
						Logger.logInfo("Connection created : user[" + arg0.getUser() + "]");
					}
				});
				connected = true;
			} catch (XMPPException exception) {			
				Logger.logException(exception);
				broadcastFailure(CREATE_CONNECTION_ERROR,null);
			}

			if ( connected ) {
				try {
					Logger.logInfo(getUserName()+" -- " + getPassword());
					connection.login(getUserName(), getPassword() );
					Logger.logInfo("Connection is created successfully !");
				} catch (XMPPException exception) {
					Logger.logException(exception);
					broadcastFailure(AUTHENTICATION_ERROR,null);
				}
			}

		} else {
			Logger.logInfo("Not connected to Internet");
			broadcastFailure(CREATE_CONNECTION_ERROR,null);
		}
	}

	private void broadcastFailure(int errorCode, Bundle extraBundle) {
		Intent intent = new Intent();
		intent.setAction(XMPP_FAILURE);
		intent.putExtra(ERROR_CODE,errorCode);
		if ( extraBundle != null ) {
			for(String key : extraBundle.keySet()) {
				intent.putExtra(key, extraBundle.getString(key));
			}
		}
		getApplicationContext().sendBroadcast(intent);
	}

	private String getPassword() {
		//TODO: later
		//		return XMPP_Activity.ME_GMAIL_PWD;
		return PreferenceHelpers.getPassword(getApplicationContext());
	}

	private String getUserName() {
		//TODO: later
		//		return XMPP_Activity.ME_GMAIL_ACC;
		return PreferenceHelpers.getUsername(getApplicationContext())+"@"+SERVICE;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ( messageToSend != null ) {
			String username = messageToSend.getNumber();
			username = Helpers.generateUsernameFromPhoneNumber(username)+"@"+SERVICE;
			Logger.logInfo("Message id to send:--" + messageToSend.getID()+"--");
			if ( connection != null && connection.isConnected() ) {
				Logger.logInfo("Connection is connected & the message is being sent !");
				waitingMessages.put(messageToSend.getID(), "");
				sendMessage(
						username,
						""+ messageToSend.getID() + SEPERATOR + messageToSend.getContent());
				//			Message msg = new Message(messageToSend.getNumber(), Message.Type.chat);
				//			msg.setBody(""+ messageToSend.getId() + SEPERATOR + messageToSend.getContent());


				WaitingThread wt = new WaitingThread(Long.parseLong(messageToSend.getID()));
				wt.start();
			} else {
				Logger.logInfo("Connection is failed !");
				Bundle extraBundle = new Bundle();
				extraBundle.putString(EXTRA_MESSAGE_ID,messageToSend.getID());
				broadcastFailure(NOT_CONNECTED_TO_SERVER, extraBundle);
				this.stopSelf();
			}
		} else {
			Logger.logInfo("Message to send is NULL");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public class WaitingThread extends Thread {
		private static final long WAITING_FOR_REPLY_TIME = 5000;
		private String id;

		public WaitingThread(long currentTime) {
			this.id = ""+currentTime;
		}

		@Override
		public void run() {
			try {				
				synchronized (this) {
					this.wait(WAITING_FOR_REPLY_TIME);
					if ( waitingMessages.containsKey(id)) {
						Logger.logInfo("Message with id: [" + id + "] is still waiting");
						timeOut(id); //remove message & failed !
					} else {
						Logger.logInfo("No message with id: [" + id + "] is waiting anymore ");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

	public void timeOut(String id) {

		Logger.logInfo("Time out !");
		synchronized (waitingMessages) {
			waitingMessages.remove(id);
		}
		Intent intent = new Intent();
		intent.setAction(XMPP_FAILURE);
		intent.putExtra(ERROR_CODE, I_MESSAGE_TIME_OUT);
		intent.putExtra(EXTRA_MESSAGE_ID, id);
		getApplicationContext().sendBroadcast(intent);

	}

}
