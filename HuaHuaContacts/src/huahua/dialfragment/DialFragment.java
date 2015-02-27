package huahua.dialfragment;

import java.lang.reflect.Method;

import huahua.huahuacontacts.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//Download by http://www.codefans.net
public class DialFragment extends Fragment{
	//≤¶∫≈º¸≈ÃµƒΩÁ√Ê
	private View DialView;
	//∫≈¬Î±‡º≠øÚ
	private EditText EditNum;
	//ÕÀ∏Ò∞¥º¸
	private Button BtnBackSpace;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		DialView = inflater.inflate(R.layout.dialfragment, null);
		
		EditNum = (EditText)DialView.findViewById(R.id.DialNumEdit);
//		EditNum.setInputType(InputType.TYPE_NULL);
		
		//edittext≤ªœ‘ æ»Ìº¸≈Ã,“™œ‘ æπ‚±Í
		if (android.os.Build.VERSION.SDK_INT <= 10) 
		{
			EditNum.setInputType(InputType.TYPE_NULL);
		} 
		else 
		{
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try 
			{
			Class<EditText> cls = EditText.class;
			Method setSoftInputShownOnFocus;
			setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
			setSoftInputShownOnFocus.setAccessible(true);
			setSoftInputShownOnFocus.invoke(EditNum, false);
			} 
			catch (Exception e) 
			{
			e.printStackTrace();
			}
		}
	
		DialView.findViewById(R.id.Btn1).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn2).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn3).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn4).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn5).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn6).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn7).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn8).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn9).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.Btn0).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.BtnXing).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.BtnJing).setOnClickListener(new BtnClick());
		DialView.findViewById(R.id.BtnDial).setOnClickListener(new BtnClick());
		
		BtnBackSpace = (Button)DialView.findViewById(R.id.BtnBackSpace);
		BtnBackSpace.setOnClickListener(new BtnClick());
		BtnBackSpace.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Vibrator vib = (Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE);
				vib.vibrate(50);
				
				EditNum.setText("");
				return false;
			}
		});
	}
	
	private class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.Btn1){
				keyPressed(KeyEvent.KEYCODE_1);
			}else if(v.getId() == R.id.Btn2){
				keyPressed(KeyEvent.KEYCODE_2);
			}else if(v.getId() == R.id.Btn3){
				keyPressed(KeyEvent.KEYCODE_3);
			}else if(v.getId() == R.id.Btn4){
				keyPressed(KeyEvent.KEYCODE_4);
			}else if(v.getId() == R.id.Btn5){
				keyPressed(KeyEvent.KEYCODE_5);
			}else if(v.getId() == R.id.Btn6){
				keyPressed(KeyEvent.KEYCODE_6);
			}else if(v.getId() == R.id.Btn7){
				keyPressed(KeyEvent.KEYCODE_7);
			}else if(v.getId() == R.id.Btn8){
				keyPressed(KeyEvent.KEYCODE_8);
			}else if(v.getId() == R.id.Btn9){
				keyPressed(KeyEvent.KEYCODE_9);
			}else if(v.getId() == R.id.Btn0){
				keyPressed(KeyEvent.KEYCODE_0);
			}else if(v.getId() == R.id.BtnXing){
				keyPressed(KeyEvent.KEYCODE_STAR);
			}else if(v.getId() == R.id.BtnJing){
				keyPressed(KeyEvent.KEYCODE_POUND);
			}else if(v.getId() == R.id.BtnDial){
				if(EditNum.length() != 0)
				{
					Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + EditNum.getText().toString()));
					startActivity(intent);
				}
				else
				{
					Toast.makeText(getActivity(), "«Î ‰»Î∫≈¬Î", Toast.LENGTH_SHORT).show();
				}
			}else if(v.getId() == R.id.BtnBackSpace){
				keyPressed(KeyEvent.KEYCODE_DEL);
			}
			
		}
	}
	
	private void keyPressed(int keyCode)
	{
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		EditNum.onKeyDown(keyCode, event);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup p = (ViewGroup) DialView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
		
		return DialView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
