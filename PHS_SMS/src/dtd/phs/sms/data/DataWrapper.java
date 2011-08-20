package dtd.phs.sms.data;

public class DataWrapper {
	public enum Type {SUMMARIES_LIST};
	private Type type;
	private Object data;
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
