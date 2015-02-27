package huahua.huahuacontacts;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/*
 * 来电去电挂断通知
 */
public class BootReceiver  extends BroadcastReceiver{
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
		{
			Intent service=new Intent(context, SmsService.class);
            context.startService(service);
		}

	}

}
