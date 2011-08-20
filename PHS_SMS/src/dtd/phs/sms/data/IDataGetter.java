package dtd.phs.sms.data;

public interface IDataGetter {
	public void onGetDataSuccess(DataWrapper wrapper);
	public void onGetDataFailed(Exception exception);
}
