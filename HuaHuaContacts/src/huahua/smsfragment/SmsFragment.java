package huahua.smsfragment;

import huahua.ContentObserver.SmssContentObserver;
import huahua.contactsfragment.SortCursor;
import huahua.contactsfragment.SortCursor.SortEntry;
import huahua.huahuacontacts.R;
import huahua.smsfragment.SmsCursor.Person_Sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SmsFragment extends Fragment{
	//通话记录的界面
	private View SmsView;
	//信息的列表
	private ListView m_smsslist;
	//信息列表的适配器
	private SmsCursorAdapter m_smsadapter;
	//加载器监听器
	private SmsLoaderListener m_SmsCallback = new SmsLoaderListener();
	//短信息内容观察者
	private SmssContentObserver SmssCO;  
	//信息联系人的数据list
	private ArrayList<Person_Sms> mPersonSmsList = new ArrayList<Person_Sms>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		SmsView = inflater.inflate(R.layout.smsfragment, null);
		
        SmssCO = new SmssContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(Uri.parse("content://sms/") , false, SmssCO);
		
        getActivity().getLoaderManager().initLoader(2,null,m_SmsCallback);
		m_smsslist = (ListView)SmsView.findViewById(R.id.sms_list);
		m_smsadapter = new SmsCursorAdapter(getActivity(), null);
		m_smsslist.setAdapter(m_smsadapter);
		m_smsslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),ChatActivity.class);
				
		        Bundle mBundle = new Bundle();  
		        
		        mBundle.putSerializable("chatperson", mPersonSmsList.get(arg2));
		        intent.putExtras(mBundle); 
				startActivity(intent);
				
			}
		});
		
	}
	
	public class SmsAdapter extends BaseAdapter{
		private LayoutInflater m_inflater;
		private ArrayList<Person_Sms> persons_sms;
    	private Context context;
		
        public SmsAdapter(Context context,
        		ArrayList<Person_Sms> persons_sms) {
    	    this.m_inflater = LayoutInflater.from(context);
    	    this.persons_sms = persons_sms;
    	    this.context = context;
        }
        
    	public void updateListView(ArrayList<Person_Sms> persons_sms){
    		this.persons_sms = persons_sms;
    		notifyDataSetChanged();
    	}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons_sms.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return persons_sms.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				 convertView = m_inflater.inflate(R.layout.sms_list_item, null);
			}
			
			 TextView sms_name = (TextView)convertView.findViewById(R.id.sms_name);
			 sms_name.setText(persons_sms.get(position).Name + 
					 "("+persons_sms.get(position).person_smss.size() + ")");
			 
			 TextView sms_content = (TextView)convertView.findViewById(R.id.sms_content);
			 sms_content.setText(persons_sms.get(position).person_smss.get(0).SMSContent);
			 
			 TextView sms_data = (TextView)convertView.findViewById(R.id.sms_date);
			 Date date = new Date(persons_sms.get(position).person_smss.get(0).SMSDate);
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date);
			 sms_data.setText(time);
			 
			 return convertView;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup p = (ViewGroup) SmsView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return SmsView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	//加载器的监听器
	private class SmsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new SmsCursorLoader(getActivity(), Uri.parse("content://sms/"), 
					null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				m_smsadapter.swapCursor(arg1);
				
				SmsCursor data = (SmsCursor)m_smsadapter.getCursor();
				mPersonSmsList = data.GetPersonSmsArray();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_smsadapter.swapCursor(null);
			
		}
	}
	
	private class SmsCursorAdapter extends CursorAdapter{
		private Context context;
		
		public SmsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.sms_list_item, parent, false);
			}
			 //短信记录的姓名
			 TextView name = (TextView)convertView.findViewById(R.id.sms_name);
			 name.setText(mPersonSmsList.get(position).Name + "("+mPersonSmsList.get(position).person_smss.size()+")");
			 
			 //短信内容
			 TextView sms_content = (TextView)convertView.findViewById(R.id.sms_content);
			 sms_content.setText(mPersonSmsList.get(position).person_smss.get(0).SMSContent);
			 
			 //通话记录的日期
			 TextView data = (TextView)convertView.findViewById(R.id.sms_date);
			 
			 Date date2 = new Date(mPersonSmsList.get(position).person_smss.get(0).SMSDate);
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date2);
			 data.setText(time);
			 
			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
			 //短信记录的姓名
//			 TextView name = (TextView)view.findViewById(R.id.sms_name);
//			 name.setText(Utils.persons_sms.get(position).Name);
//			 
//			 //短信内容
//			 TextView sms_content = (TextView)convertView.findViewById(R.id.sms_content);
//			 sms_content.setText(Utils.persons_sms.get(position).person_smss.get(0).SMSContent);
//			 
//			 //通话记录的日期
//			 TextView data = (TextView)convertView.findViewById(R.id.sms_date);
//			 
//			 Date date2 = new Date(Utils.persons_sms.get(position).person_smss.get(0).SMSDate);
//			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
//			 String time = sfd.format(date2);
//			 data.setText(time);
			 
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.sms_list_item, parent, false);
		}
	}
	
}
