package huahua.contactsfragment;
//Download by http://www.codefans.net
import java.util.ArrayList;

import huahua.contactsfragment.ContactsFragment.FilterAdapter;
import huahua.contactsfragment.SortCursor.SortEntry;
import huahua.huahuacontacts.R;
import huahua.huahuacontacts.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BatchDeleteActivity extends Activity{
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
	//所有联系人的数据list
	private ArrayList<SortEntry> mSortList = new ArrayList<SortEntry>();
	//筛选查询后的数据list
	private ArrayList<SortEntry> mFilterList = new ArrayList<SortEntry>();
	//加载器监听器
	private ContactsLoaderListener m_loaderCallback = new ContactsLoaderListener();
	//搜索过滤联系人EditText
	private EditText m_FilterEditText;
	//没有匹配联系人时显示的TextView
	private TextView m_listEmptyText;
	//最上面的layout
	private FrameLayout m_topcontactslayout;
	//选中的联系人名字
	private String ChooseContactName;
	//选中的联系人号码
	private String ChooseContactNumber;
	//选中的联系人ID
	private String ChooseContactID;
	//加载对话框
	private ProgressDialog m_dialogLoading;
	//批量删除按钮
	private Button m_DeleteNumBtn;
	//选中全部按钮
	private Button m_SelectAllBtn;
	//选中多少个需要删除的联系人
	private int m_choosenum=0;
	//id的数组
	private ArrayList<String> ChooseContactsID = new ArrayList<String>();
	//选择所有联系人的标志
	private boolean m_selectAll = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.batchdelete_contacts);
		
		//得到字母列的对象,并设置触摸响应监听器
		m_asb = (AlphabetScrollBar)findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView)findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);
		
		//得到联系人列表,并设置适配器
		getLoaderManager().initLoader(0,null,m_loaderCallback);
		m_contactslist = (ListView)findViewById(R.id.pb_listvew);
		m_contactsAdapter = new ContactsCursorAdapter(this, null);
		m_contactslist.setAdapter(m_contactsAdapter);
		m_contactslist.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(mSortList.get(arg2).mchoose == false)
				{
					mSortList.get(arg2).mchoose = true;
					m_choosenum++;
					ChooseContactsID.add(mSortList.get(arg2).mID);
				}
				else
				{
					mSortList.get(arg2).mchoose = false;
					for(int i=0;i<m_choosenum;i++)
					{
						if(ChooseContactsID.get(i).equals(mSortList.get(arg2).mID))
						{
							ChooseContactsID.remove(i);
							break;
						}
					}
					m_choosenum--;
				}
				
				m_contactsAdapter.notifyDataSetChanged();
				m_DeleteNumBtn.setText("删除("+ m_choosenum +")");
			}
		});
		
		m_listEmptyText = (TextView)findViewById(R.id.nocontacts_notice);
		m_topcontactslayout = (FrameLayout)findViewById(R.id.top_contacts_layout);
		m_DeleteNumBtn = (Button)findViewById(R.id.delete_num);
		m_SelectAllBtn = (Button)findViewById(R.id.select_all);
		m_DeleteNumBtn.setOnClickListener(new BtnClick());
		m_SelectAllBtn.setOnClickListener(new BtnClick());
		
    	//初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText = (EditText)findViewById(R.id.pb_search_edit);
		m_FilterEditText.setVisibility(View.GONE);
		m_FilterEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				if(!"".equals(s.toString().trim()))
				{  
					//根据编辑框值过滤联系人并更新联系列表
					SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
					mFilterList = data.FilterSearch(s.toString().trim());
					
					m_FAdapter = new FilterAdapter(BatchDeleteActivity.this, mFilterList);
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
	
	private class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.delete_num)
			{
				if(m_choosenum>0)
				{
					AlertDialog DeleteDialog = new AlertDialog.Builder(BatchDeleteActivity.this). 
			                setTitle("删除"). 
			                setMessage("删除"+m_choosenum +"个联系人?").
			                setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//删除联系人操作,放在线程中处理
									new DeleteContactsTask().execute();
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
				else
				{
					Toast.makeText(BatchDeleteActivity.this, "请选择要删除的联系人", Toast.LENGTH_LONG).show();
				}
			}
			else if(v.getId() == R.id.select_all)
			{
				ChooseContactsID.clear();
				if(!m_selectAll)
				{
					for(int i=0;i<mSortList.size();i++)
					{
						mSortList.get(i).mchoose = true;
						ChooseContactsID.add(mSortList.get(i).mID);
					}
					m_choosenum = mSortList.size();
					m_DeleteNumBtn.setText("删除("+ mSortList.size() +")");
					m_SelectAllBtn.setText("取消全部");
					m_contactsAdapter.notifyDataSetChanged();
					m_selectAll = !m_selectAll;
				}
				else
				{
					for(int i=0;i<mSortList.size();i++)
					{
						mSortList.get(i).mchoose = false;
					}
					m_choosenum = 0;
					m_DeleteNumBtn.setText("删除(0)");
					m_SelectAllBtn.setText("选择全部");
					m_contactsAdapter.notifyDataSetChanged();
					m_selectAll = !m_selectAll;
				}

			}
			
		}
		
	}
	
	private class  DeleteContactsTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<ContentProviderOperation> ops =
	          new ArrayList<ContentProviderOperation>();
			
			for(int i=0;i<ChooseContactsID.size();i++)
			{
				ops.add(ContentProviderOperation.newDelete( 
						Uri.withAppendedPath(RawContacts.CONTENT_URI,ChooseContactsID.get(i))).build());
//				ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
//				          .withSelection(Data.RAW_CONTACT_ID + "=?", new String[]{ChooseContactsID.get(i)})
//				          .build());
			}
			 
			 try {
				getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(m_dialogLoading!= null)
			{
				m_dialogLoading.dismiss();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(BatchDeleteActivity.this);  
	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
	        m_dialogLoading.setMessage("正在删除");  
	        m_dialogLoading.setCancelable(false);
           m_dialogLoading.show();  
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
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
			if(convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
			}
			
            TextView name = (TextView) convertView.findViewById(R.id.contacts_name);
            name.setText(mSortList.get(position).mName);
    	    
            TextView number = (TextView) convertView.findViewById(R.id.contacts_number);
            number.setText(mSortList.get(position).mNum);
			
			if(mSortList.get(position).mchoose == true)
			{
				ImageView choosecontact = (ImageView)convertView.findViewById(R.id.choose_contact);
				choosecontact.setImageResource(R.drawable.cb_checked);
			}
			else
			{
				ImageView choosecontact = (ImageView)convertView.findViewById(R.id.choose_contact);
				choosecontact.setImageResource(R.drawable.cb_unchecked);
			}

			return convertView;
//			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
//            TextView name = (TextView) view.findViewById(R.id.contacts_name);
//            name.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
//    	    
//            TextView number = (TextView) view.findViewById(R.id.contacts_number);
//            number.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
		}
		
	}
	
	//加载器的监听器
	private class ContactsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new SortCursorLoader(BatchDeleteActivity.this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			m_contactsAdapter.swapCursor(arg1);
			
			if(m_topcontactslayout.getVisibility() == View.VISIBLE)
			{
				SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
				mSortList = data.GetContactsArray();
			}
			else
			{
				SortCursor data = (SortCursor)m_contactsAdapter.getCursor();
				mFilterList = data.FilterSearch(m_FilterEditText.getText().toString().trim());
				m_FAdapter = new FilterAdapter(BatchDeleteActivity.this, mFilterList);
				m_contactslist.setAdapter(m_FAdapter);
			}
			
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_contactsAdapter.swapCursor(null);
			
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
