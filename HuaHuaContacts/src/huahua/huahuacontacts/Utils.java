package huahua.huahuacontacts;

import huahua.contactsfragment.SortCursor.SortEntry;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;

public class Utils {
	public static Context m_context;
	//������ϵ�˵�����list
	public static ArrayList<SortEntry> mPersons = new ArrayList<SortEntry>();
	
	//��ʼ��������Activity��������
	public static void init(Context context)
	{
		m_context = context;
	}

	//�����ݿ���������ϵ��
	public static void AddContact(String name, String number)
	{
		ContentValues values = new ContentValues(); 
        //������RawContacts.CONTENT_URIִ��һ����ֵ���룬Ŀ���ǻ�ȡϵͳ���ص�rawContactId  
        Uri rawContactUri = m_context.getContentResolver().insert(RawContacts.CONTENT_URI, values); 
        long rawContactId = ContentUris.parseId(rawContactUri); 
        //��data������������� 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId);  
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);//�������� 
        values.put(StructuredName.GIVEN_NAME, name); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
       
        //��data�����绰���� 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId); 
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE); 
        values.put(Phone.NUMBER, number); 
        values.put(Phone.TYPE, Phone.TYPE_MOBILE); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values); 
        
	}
	
	//�������ݿ�����ϵ��
	public static void ChangeContact(String name, String number, String ContactId)
	{
		Log.i("huahua", name);
		ContentValues values = new ContentValues();
		// ��������
        values.put(StructuredName.GIVEN_NAME, name);
        m_context.getContentResolver().update(ContactsContract.Data.CONTENT_URI,
                        values,
                        Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE  + "=?",
                        new String[] { ContactId,StructuredName.CONTENT_ITEM_TYPE });
		
		//���µ绰
        values.clear();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        m_context.getContentResolver().update(ContactsContract.Data.CONTENT_URI,
				values, 
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE  + "=?",
				new String[] { ContactId,Phone.CONTENT_ITEM_TYPE});
	}
	
	//ɾ����ϵ��
	public static void DeleteContact(String ContactId)
	{
      Uri uri = Uri.withAppendedPath(RawContacts.CONTENT_URI, ContactId);   
		m_context.getContentResolver().delete(uri, null, null);

		//��ɾ��������ȫ��
//		m_context.getContentResolver().delete(ContactsContract.Data.CONTENT_URI,
//				Data.RAW_CONTACT_ID + "=? ",
//				new String[] { ContactId});
	}
	
	//���������ݿ����������͵Ķ���
	public static void AddSms(String Number,String Content,Long Date,int Type)
	{
		ContentValues values = new ContentValues();
		values.put("address", Number);
		values.put("body", Content);
		values.put("date", Date);
		values.put("type", Type);
		Uri result = m_context.getContentResolver().insert(Uri.parse("content://sms/"), values);
	}
}
