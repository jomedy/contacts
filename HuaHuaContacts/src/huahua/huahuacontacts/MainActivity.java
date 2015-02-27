package huahua.huahuacontacts;

import huahua.calllogfragment.CallLogsFragment;
import huahua.contactsfragment.ContactsFragment;
import huahua.dialfragment.DialFragment;
import huahua.smsfragment.SmsFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

	private FragmentTabHost mTabHost = null;;
	private View indicator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_main);
		
		Utils.init(this);
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// ���tab���ƺ�ͼ��
		indicator = getIndicatorView("���ż���", R.layout.dial_indicator);
		mTabHost.addTab(mTabHost.newTabSpec("Dial").setIndicator(indicator), 
				DialFragment.class, null);
		
		indicator = getIndicatorView("��ϵ��", R.layout.contacts_indicator);
		mTabHost.addTab(mTabHost.newTabSpec("Contacts").setIndicator(indicator), 
				ContactsFragment.class, null);
		
		indicator = getIndicatorView("ͨ����¼", R.layout.calllogs_indicator);
		mTabHost.addTab(mTabHost.newTabSpec("CallLogs").setIndicator(indicator),
				CallLogsFragment.class, null);
		
		indicator = getIndicatorView("��Ϣ", R.layout.sms_indicator);
		mTabHost.addTab(mTabHost.newTabSpec("Sms").setIndicator(indicator),
				SmsFragment.class, null);
		
		//��һ�ν��붼�ǽ�����ϵ��
		mTabHost.setCurrentTab(1);

	}

	private View getIndicatorView(String name, int layoutId) {
		View v = getLayoutInflater().inflate(layoutId, null);
		TextView tv = (TextView) v.findViewById(R.id.tabText);
		tv.setText(name);
		return v;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTabHost = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {  
            moveTaskToBack(false);  
            return true;  
        } 
		return super.onKeyDown(keyCode, event);
	}
	
}
