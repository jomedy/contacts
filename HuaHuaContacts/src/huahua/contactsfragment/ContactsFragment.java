package huahua.contactsfragment;

import huahua.ContentObserver.ContactsContentObserver;
import huahua.contactsfragment.SortCursor.SortEntry;
import huahua.huahuacontacts.R;
import huahua.huahuacontacts.Utils;
import huahua.smsfragment.ChatActivity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsFragment extends Fragment {
	//联系人列表的界面
	private View ContactsView;
	//字母列视图View
	private AlphabetScrollBar m_asb;
	//显示选中的字母
	private TextView m_letterNotice;
	//联系人的列表
	private ListView m_contactslist;
	//联系人列表的适配器
	private ContactsCursorAdapter m_contactsAdapter;
	//筛选后的适配器
	private FilterAdapter m_FAdapter;
	//筛选查询后的数据list
	private ArrayList<SortEntry> mFilterList = new ArrayList<SortEntry>();
	//加载器监听器
	private ContactsLoaderListener m_ContactsCallback = new ContactsLoaderListener();
	//搜索过滤联系人EditText
	private EditText m_FilterEditText;
	//没有匹配联系人时显示的TextView
	private TextView m_listEmptyText;
	//新增联系人按钮
	private ImageButton m_AddContactBtn;
	//最上面的layout
	private FrameLayout m_topcontactslayout;
	//联系人内容观察者
	private ContactsContentObserver ContactsCO;  
	//选中的联系人名字
	private String ChooseContactName;
	//选中的联系人号码
	private String ChooseContactNumber;
	//选中的联系人ID
	private String ChooseContactID;
	//加载对话框
	ProgressDialog m_dialogLoading;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ContactsView = inflater.inflate(R.layout.contactsfragment, null);
		
		setHasOptionsMenu(true);
		
        ContactsCO = new ContactsContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false, ContactsCO); 
		
		//得到字母列的对象,并设置触摸响应监听器
		m_asb = (AlphabetScrollBar)ContactsView.findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView)ContactsView.findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);
		
		//得到联系人列表,并设置适配器
		getActivity().getLoaderManager().initLoader(0,null,m_ContactsCallback);
		m_contactslist = (ListView)ContactsView.findViewById(R.id.pb_listvew);
		m_contactsAdapter = new ContactsCursorAdapter(getActivity(), null);
		m_contactslist.setAdapter(m_contactsAdapter);
		
		//联系人列表长按监听
		m_contactslist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				Vibrator vib = (Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE);
				vib.vibrate(50);
				
			
				
				if(m_topcontactslayout.getVisibility() == View.VISIBLE)
				{
					SortCursor ContactsCursor = (SortCursor)m_contactsAdapter.getCursor();
					ChooseContactName = ContactsCursor.getName(arg2);
					ChooseContactNumber = ContactsCursor.getNumber(arg2);
					ChooseContactID = ContactsCursor.getID(arg2);
				}
				else
				{
					ChooseContactName = mFilterList.get(arg2).mName;
					ChooseContactNumber = mFilterList.get(arg2).mNum;
					ChooseContactID = mFilterList.get(arg2).mID;
				}
				
//				final Person_Sms personsms = new Person_Sms();
//				personsms.Name = ChooseContactName;
//				personsms.Number = ChooseContactNumber;
//				for(int i=0;i<Utils.persons_sms.size();i++)
//				{
//					if(personsms.Name.equals(Utils.persons_sms.get(i).Name))
//					{
//						personsms.person_smss = Utils.persons_sms.get(i).person_smss;
//						break;
//					}
//				}
				
				AlertDialog ListDialog = new AlertDialog.Builder(getActivity()). 
		                setTitle(ChooseContactName).
		                setItems(new String[] { "拨号","短信","删除联系人","编辑联系人"}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which == 0)
								{
									Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + ChooseContactNumber));
									startActivity(intent);
								}
								else if(which == 1)
								{
									Intent intent = new Intent(getActivity(),ChatActivity.class);
							        Bundle mBundle = new Bundle();  
//							        mBundle.putSerializable("chatperson", personsms);
							        intent.putExtras(mBundle); 
									startActivity(intent);
								}
								else if(which == 2)
								{
									AlertDialog DeleteDialog = new AlertDialog.Builder(getActivity()). 
							                setTitle("删除"). 
							                setMessage("删除联系人"+ChooseContactName +"?").
							                setPositiveButton("确定", new DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													//删除联系人操作,放在线程中处理
													new DeleteContactTask().execute();
												}
											}).
											setNegativeButton("取消", new DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													
												}
											}).
											create(); 
									DeleteDialog.show(); 
								}
								else if(which == 3)
								{
									Bundle bundle = new Bundle();
									bundle.putInt("tpye", 1);
									bundle.putString("id", ChooseContactID);
									bundle.putString("name", ChooseContactName);
									bundle.putString("number", ChooseContactNumber);
									
									Intent intent = new Intent(getActivity(), AddContactsActivity.class);
									intent.putExtra("person", bundle);
									startActivity(intent);
								}
							}
						}).
						create(); 
					ListDialog.show(); 
		        
				return false;
			}
		});
		
		m_listEmptyText = (TextView)ContactsView.findViewById(R.id.nocontacts_notice);
		
		//新增联系人
		m_AddContactBtn = (ImageButton)ContactsView.findViewById(R.id.add_contacts);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		
		m_topcontactslayout = (FrameLayout)ContactsView.findViewById(R.id.top_contacts_layout);
		
    	//初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText = (EditText)ContactsView.findViewById(R.id.pb_search_edit);
		m_FilterEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				if(!"".equals(s.toString().trim()))
				{  
					//根据编辑框值过滤联系人并更新联系列表
					SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
					mFilterList = data.FilterSearch(s.toString().trim());
					
					m_FAdapter = new FilterAdapter(getActivity(), mFilterList);
					m_contactslist.setAdapter(m_FAdapter);
					
					m_asb.setVisibility(View.GONE);
					m_topcontactslayout.setVisibility(View.GONE);
				}
				else
				{
					m_contactslist.setAdapter(m_contactsAdapter);
					m_topcontactslayout.setVisibility(View.VISIBLE);
					m_asb.setVisibility(View.VISIBLE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	 @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	      menu.add(0, 1, 1, "批量删除");
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 1)
		{
			Intent intent = new Intent(getActivity(), BatchDeleteActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class  DeleteContactTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Utils.DeleteContact(ChooseContactID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(m_dialogLoading!= null)
			{
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(getActivity());  
	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
	        m_dialogLoading.setMessage("正在删除");  
	        m_dialogLoading.setCancelable(false);
           m_dialogLoading.show();  
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			 Log.i("huahua", "onProgressUpdate"); 
		}
		
	}

	private class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v == m_AddContactBtn)
			{
				Bundle bundle = new Bundle();
				bundle.putInt("tpye", 0);
				bundle.putString("name", "");

				bundle.putString("number", "");
				
				Intent intent = new Intent(getActivity(), AddContactsActivity.class);
				intent.putExtra("person", bundle);
				startActivity(intent);
			}
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup p = (ViewGroup) ContactsView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return ContactsView;
	}
	
	//加载器的监听器
	private class ContactsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new SortCursorLoader(getActivity(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				m_contactsAdapter.swapCursor(arg1);
			
			SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
			if(m_topcontactslayout.getVisibility() == View.VISIBLE)
			{
				Utils.mPersons = data.GetContactsArray();
			}
			else
			{
				mFilterList = data.FilterSearch(m_FilterEditText.getText().toString().trim());
//				m_FAdapter.notifyDataSetChanged();
				m_FAdapter = new FilterAdapter(getActivity(), mFilterList);
				m_contactslist.setAdapter(m_FAdapter);
			}
			
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_contactsAdapter.swapCursor(null);
			
		}
		
	}
	
	private class ContactsCursorAdapter extends CursorAdapter{
		int ItemPos = -1;
		private ArrayList<SortCursor.SortEntry> list;
		private Context context;
		
		public ContactsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemPos = position;

			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
            TextView name = (TextView) view.findViewById(R.id.contacts_name);
            name.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
    	    
            TextView number = (TextView) view.findViewById(R.id.contacts_number);
            number.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            
            ImageView chooseView = (ImageView)view.findViewById(R.id.choose_contact);
            chooseView.setVisibility(View.GONE);
            
			//字母提示textview的显示 
			TextView letterTag = (TextView)view.findViewById(R.id.pb_item_LetterTag);
			//获得当前姓名的拼音首字母
			String firstLetter = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).substring(0,1).toUpperCase();
			
			//如果是第1个联系人 那么letterTag始终要显示
			if(ItemPos == 0)
			{
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			}			
			else
			{
				//获得上一个姓名的拼音首字母
				cursor.moveToPrevious();
				String firstLetterPre = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).substring(0,1).toUpperCase();
				//比较一下两者是否相同
				if(firstLetter.equals(firstLetterPre))
				{
					letterTag.setVisibility(View.GONE);
				}
				else 
				{
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
		}
		
	}
	
    class FilterAdapter extends BaseAdapter{
    	
    	private LayoutInflater mInflater;
    	private ArrayList<SortCursor.SortEntry> data;
    	private Context context;
    	
        public FilterAdapter(Context context,
        		ArrayList<SortCursor.SortEntry> data) {
    	    this.mInflater = LayoutInflater.from(context);
    	    this.data = data;
    	    this.context = context;
        }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = mInflater.inflate(R.layout.contacts_list_item, null);
			
			//姓名显示
			TextView nameCtrl = (TextView)arg1.findViewById(R.id.contacts_name);			
			String strName = data.get(arg0).mName;
			nameCtrl.setText(strName);
			
			//电话号码显示
			TextView numberCtrl = (TextView)arg1.findViewById(R.id.contacts_number);
			String strNumber = data.get(arg0).mNum;
			numberCtrl.setText(strNumber);
			
            ImageView chooseView = (ImageView)arg1.findViewById(R.id.choose_contact);
            chooseView.setVisibility(View.GONE);
			
			return arg1;
		}
    }
	
	//字母列触摸的监听器
	private class ScrollBarListener implements AlphabetScrollBar.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {
			
			//触摸字母列时,将联系人列表更新到首字母出现的位置
			SortCursor ContactsCursor = (SortCursor)m_contactsAdapter.getCursor();
			if(ContactsCursor != null) 
			{
				int idx = ContactsCursor.binarySearch(letter);
				if(idx != -1)
				{
					m_contactslist.setSelection(idx);
				}
			}
		}
	}

}
