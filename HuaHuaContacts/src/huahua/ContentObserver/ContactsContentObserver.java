package huahua.ContentObserver;

import huahua.huahuacontacts.Utils;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class ContactsContentObserver extends ContentObserver{

	public ContactsContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		Log.i("huahua", "��ϵ�����ݿⷢ���˱仯");
//		Utils.getContacts();
	}

}
