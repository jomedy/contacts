package huahua.smsfragment;

import huahua.huahuacontacts.Utils;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

public class SmsCursor extends CursorWrapper{
	private Cursor mCursor;  
	private Context  mcontext;
	private ArrayList<Person_Sms> mPerson_SmsList;
	private int mPos;
	
	/*
	 *����Ϣ������
	 */
	public static class SMSs implements Serializable{
		public String SMSContent;      //��������
		public Long SMSDate;      //����ʱ��
		public Integer SMSType;      //����״̬
		public int mOrder;  //��ԭCursor�е�λ��
	} 
	
	//������ϵ�˵���Ϣ����
	public static class Person_Sms implements Serializable {
		public String Name;  //����
		public String Number;      //�绰����
		public ArrayList<SMSs> person_smss= new ArrayList<SMSs>();   
	} 
	
	public SmsCursor(Cursor cursor, Context context) {
		super(cursor);
		// TODO Auto-generated constructor stub
		mCursor = cursor;
		mcontext = context;
		
		mPerson_SmsList = new ArrayList<Person_Sms>();
        for(cursor.moveToFirst(); ! cursor.isAfterLast();  cursor.moveToNext()) 
        {
        	Boolean AddPersonFlag = true;
        	Person_Sms  person_sms = new Person_Sms();
    		SMSs Sms = new SMSs();
            
    		person_sms.Number = cursor.getString(cursor.getColumnIndex("address"));
            Sms.SMSContent = cursor.getString(cursor.getColumnIndex("body"));
    		Sms.SMSDate = cursor.getLong(cursor.getColumnIndex("date"));
    		Sms.SMSType = cursor.getInt(cursor.getColumnIndex("type"));
    		
            if(person_sms.Number != null)
            {
            	String NumberRemove86 = null;
        		if (person_sms.Number.contains("+86")) //�������+86�ĺ��룬��Ϊ��Щ���ŷ��Ļ���ǩ����+86
        		{
        			NumberRemove86 = person_sms.Number.substring(3, person_sms.Number.length());
        		}
        		else
        		{
        			NumberRemove86 = "+86" + person_sms.Number;
        		}
            	
                for(int i=0;i<mPerson_SmsList.size();i++)
                {
            	   if(mPerson_SmsList.get(i).Number.equals(person_sms.Number)
            			|| mPerson_SmsList.get(i).Number.equals(NumberRemove86))
            	   {
            		   AddPersonFlag = false;
            		   mPerson_SmsList.get(i).person_smss.add(Sms);
            		   break;
            	   }
                }
                
                if(AddPersonFlag)
                {
                	for(int i=0;i<Utils.mPersons.size();i++)
                	{
                		if(Utils.mPersons.get(i).mNum.equals(person_sms.Number)
                			|| Utils.mPersons.get(i).mNum.equals(NumberRemove86))
                		{
                			person_sms.Name = Utils.mPersons.get(i).mName;
                			break;
                		}
                	}
            		if(person_sms.Name == null)
            		{
            			person_sms.Name = person_sms.Number;
            		}
                	
                	person_sms.person_smss.add(Sms);
                	mPerson_SmsList.add(person_sms);
                }
            }
       
        }
        
	}
	
	@Override
	public boolean moveToPosition(int position) {
		// TODO Auto-generated method stub
		mPos = position; 
		return super.moveToPosition(position);
	}

	@Override
	public boolean moveToFirst() {
		// TODO Auto-generated method stub
		return moveToPosition(0);
	}

	@Override
	public boolean moveToLast() {
		// TODO Auto-generated method stub
		return moveToPosition(getCount() - 1); 
	}

	@Override
	public boolean moveToNext() {
		// TODO Auto-generated method stub
		return moveToPosition(mPos + 1);
	}

	public ArrayList<Person_Sms> GetPersonSmsArray() {
		 
    	return mPerson_SmsList;
	}

}
