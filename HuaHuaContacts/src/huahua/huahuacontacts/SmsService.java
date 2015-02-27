package huahua.huahuacontacts;

import huahua.smsfragment.SmsCursor.SMSs;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SmsService extends Service{
	 private TelephonyManager telephonyManager;  
	 private DynamicReceiver dynamicReceiver; 
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		IntentFilter localIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        localIntentFilter.setPriority(Integer.MAX_VALUE);
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, localIntentFilter);
        
        Log.v("huahua", "TelephonyService启动"+ System.currentTimeMillis());  
        
        super.onCreate();
	}
	
	public class DynamicReceiver extends BroadcastReceiver {  
	    public void onReceive(Context context, Intent intent) {  
	        String action = intent.getAction();  
	        if("android.provider.Telephony.SMS_RECEIVED".equals(action)){  
	        	Log.v("huahua", "TelephonyService收到短信程序");  
	        	
	            //这里震动500毫秒,说明短信来了
	    		Vibrator vib = (Vibrator)SmsService.this.getSystemService(Service.VIBRATOR_SERVICE);
	    		vib.vibrate(500);
	        	
				StringBuilder sb = new StringBuilder();
				Bundle bundle = intent.getExtras();
					 
				 SMSs new_sm =  new SMSs();
				 new_sm.SMSContent = "";
				 String Number = null;
	             if (bundle != null) 
	             {
	                  Object[] pdus = (Object[]) bundle.get("pdus");
	                  SmsMessage[] msg = new SmsMessage[pdus.length];
	                  for (int i = 0; i < pdus.length; i++) 
	                  {
	                      msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
	                  }
	                  for (SmsMessage currMsg : msg) 
	                  {
	                	  Number = currMsg.getDisplayOriginatingAddress();
	                	  new_sm.SMSDate = currMsg.getTimestampMillis();
	                	  new_sm.SMSContent = new_sm.SMSContent + currMsg.getDisplayMessageBody();
	                	  new_sm.SMSType = 1;
	               	  }
	                  
						Intent i = new Intent("android.huahua.SMS_RECEIVED");
						i.putExtras(bundle);
						sendBroadcast(i);
	                  
//						ContentValues values = new ContentValues();
//						values.put("address", Number);
//						values.put("body", new_sm.SMSContent);
//						values.put("date", new_sm.SMSDate);
//						values.put("type", new_sm.SMSType );
//						Uri result = context.getContentResolver().insert(Uri
//								.parse("content://sms/"), values);
	             }
	        	
	        }  
	    }  
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if(state == TelephonyManager.CALL_STATE_IDLE)//空闲
			{
				Log.i("huahua", "CALL_STATE_IDLE");
//				Utils.getCallLogs();
			}
			else if(state == TelephonyManager.CALL_STATE_RINGING)//来电
			{
				Log.i("huahua", "CALL_STATE_RINGING");
			}
			else if(state == TelephonyManager.CALL_STATE_OFFHOOK)//去电,通话中
			{
				Log.i("huahua", "CALL_STATE_OFFHOOK");
			}
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
