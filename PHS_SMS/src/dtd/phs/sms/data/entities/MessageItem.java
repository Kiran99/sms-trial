package dtd.phs.sms.data.entities;

import dtd.phs.sms.message_center.AndroidSMSSender.ResultCode;


public class MessageItem {

	private ResultCode resultCode;
	private String number;
	private String content;
	private String id;

	public ContactItem getContact() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContent() {
		return this.content;
	}

	public void setResultCode(ResultCode code) {
		this.resultCode = code;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String num) {
		this.number = num;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getID() {
		return id;
	}

}
