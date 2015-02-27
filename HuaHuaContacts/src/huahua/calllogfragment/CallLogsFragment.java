package huahua.calllogfragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import huahua.ContentObserver.CallLogsContentObserver;
import huahua.huahuacontacts.R;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallLogsFragment extends Fragment{
	//通话记录的界面
	private View CallLogView;
	//通话记录的列表
	private ListView m_calllogslist;
	//通话记录列表的适配器
	private CallLogsCursorAdapter m_calllogsadapter;
	//加载器监听器
	private CallLogsLoaderListener m_CallLogsCallback = new CallLogsLoaderListener();
	//通话记录内容观察者
	private CallLogsContentObserver CallLogsCO;  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		CallLogView = inflater.inflate(R.layout.calllogsfragment, null);
		
        CallLogsCO = new CallLogsContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI , false, CallLogsCO); 
		
		//通话记录列表
        getActivity().getLoaderManager().initLoader(1,null,m_CallLogsCallback);
		m_calllogslist = (ListView)CallLogView.findViewById(R.id.calllogs_list);
		m_calllogsadapter = new CallLogsCursorAdapter(getActivity(), null);
		m_calllogslist.setAdapter(m_calllogsadapter);
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) CallLogView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return CallLogView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	//加载器的监听器
	private class CallLogsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, 
					null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			m_calllogsadapter.swapCursor(arg1);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_calllogsadapter.swapCursor(null);
			
		}
		
	}
	
	private class CallLogsCursorAdapter extends CursorAdapter{
		private Context context;
		
		public CallLogsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
			 //通话记录的姓名
			 TextView name = (TextView)view.findViewById(R.id.calllog_name);
			 name.setText(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
			 
			if(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null){
				name.setText("未知号码");
			}else{
				name.setText(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
			}
			 
			 //通话记录的电话状态
			ImageView Type = (ImageView)view.findViewById(R.id.calllog_type);
			 if(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.INCOMING_TYPE)
			 {
				 Type.setBackgroundResource(R.drawable.ic_calllog_incomming_normal);
			 }
			 else if(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.OUTGOING_TYPE)
			 {
				 Type.setBackgroundResource(R.drawable.ic_calllog_outgoing_nomal);
			 }
			 else if(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.MISSED_TYPE)
			 {
				 Type.setBackgroundResource(R.drawable.ic_calllog_missed_normal);
			 }
			 
			 //通话记录的号码
			 TextView number = (TextView)view.findViewById(R.id.calllog_number);
			 number.setText(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
			 
			 //通话记录的日期
			 TextView data = (TextView)view.findViewById(R.id.calllog_data);
			 
			 Date date2 = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date2);
			 data.setText(time);
			 
			ImageButton dialBtn = (ImageButton)view.findViewById(R.id.calllog_dial);
			dialBtn.setTag(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
			dialBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + (String)arg0.getTag()));
						startActivity(intent);
				}
			});
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.calllogs_list_item, parent, false);
		}
		
	}

}
