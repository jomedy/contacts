package huahua.ContentObserver;

import huahua.huahuacontacts.Utils;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class CallLogsContentObserver extends ContentObserver{

	public CallLogsContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		Log.i("huahua", "ͨ����¼���ݿⷢ���˱仯");
//		Utils.getCallLogs();
	}

}
