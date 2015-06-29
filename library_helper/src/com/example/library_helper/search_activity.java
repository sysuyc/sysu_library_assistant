package com.example.library_helper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import com.example.library_helper.controller.DBSearchController;
import com.example.library_helper.controller.InternetSearchController;
import com.example.library_helper.controller.SearchController;
import com.example.library_helper.model.Book;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class search_activity extends Activity{
	private EditText search_content;
	private RadioGroup group;
	private Button search_btn;
	private Button scanning_btn;
	private Intent mIntent;
	private int search_type;
	private ProgressDialog mProgressDialog;
	private Handler mHandler;
	private List<Book> dataList;
	private SearchController sc;
	ExecutorService executorService;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		mHandler = new Handler() {
        	@Override
        	public void handleMessage(android.os.Message msg) {
        		
        		mProgressDialog.dismiss();
        		
        		
        		if(msg.what == 1) {
        			dataList = (List<Book>)msg.obj;
            		if(dataList != null && dataList.size() > 0) {
            			Bundle mBundle = new Bundle();
            			mBundle.putString("search_content", search_content.getText().toString());
            			mBundle.putInt("search_type", search_type);
            			
            			String[] strBooks = new String[dataList.size()];
            			for(int i = 0; i < dataList.size(); i++) {
            				Book b = dataList.get(i);
            				strBooks[i] = b.ChangeToString();
            			}
            			
            			mBundle.putStringArray("search_result", strBooks);
            			
        				mIntent = new Intent();
        				mIntent.setClass(search_activity.this, result_activity.class);
        				mIntent.putExtras(mBundle);
        				startActivity(mIntent);				
        				search_activity.this.finish();
            		} else {
            			Toast.makeText(search_activity.this, "��ѯ���Ϊ��", Toast.LENGTH_SHORT).show();
            		}
        		} else if(msg.what == 2) {
        			Toast.makeText(search_activity.this, "����ʱ", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 3) {
        			Toast.makeText(search_activity.this, "��ȡ���ݳ�ʱ", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 4) {
        			Toast.makeText(search_activity.this, "�������", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 5) {
        			Toast.makeText(search_activity.this, "���ݷ��ش���", Toast.LENGTH_SHORT).show();
        		} else {
        			Toast.makeText(search_activity.this, "δ֪����", Toast.LENGTH_SHORT).show();
        		}
        	}
		};
		
		executorService = Executors.newSingleThreadExecutor();
		sc = new SearchController(this, mHandler);
		
		search_content = (EditText)this.findViewById(R.id.editText1);
		
		group = (RadioGroup)this.findViewById(R.id.radioGroup);
		if(group.getCheckedRadioButtonId() == R.id.radioButton1) {
			//choose course
			search_type = 1;
		} else if(group.getCheckedRadioButtonId() == R.id.radioButton2) {
			//choose bookname
			search_type = 2;
		} else {
			//error
			search_type = 0;
		}
		
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1 == R.id.radioButton1) {					
					//ѡ���˿γ�
					search_type = 1;
					//Toast.makeText(search_activity.this, "���ݿγ̽�������", Toast.LENGTH_SHORT).show();
				}
				else if(arg1 == R.id.radioButton2) {
					//ѡ��������
					search_type = 2;
					//Toast.makeText(search_activity.this, "����������������", Toast.LENGTH_SHORT).show();
				} else {
					//error
					search_type = 0;
				}
			}
		});
		
		
		
		search_btn = (Button)findViewById(R.id.search_btn);
		search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//search
				if(search_type == 1 || search_type == 2) {
					//search
					String sitem = search_content.getText().toString().trim();
					if(sitem.equals("")) {
						Toast.makeText(search_activity.this, "�����������ؼ���", Toast.LENGTH_SHORT).show();
					} else {
						//mProgressDialog = ProgressDialog.show(search_activity.this, "Requesting", "Requesting...");
						//Toast.makeText(search_activity.this, "��ʼ��ѯ", Toast.LENGTH_SHORT).show();
						sc.setSearchType(search_type);
						sc.setSearchItem(sitem);
						
						mProgressDialog = ProgressDialog.show(search_activity.this, "Requesting", "Requesting...");
						executorService.submit(sc);
						
					}
				} else {
					//error
					Toast.makeText(search_activity.this, "��ѡ���ѯ��ʽ", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		scanning_btn = (Button) this.findViewById(R.id.scanningButton);
		scanning_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchScanningActivity();
			}
		});
	}
	
	private void launchScanningActivity() {
		Intent intent = new Intent(this, CaptureActivity.class);  //CaptureActivity��ɨ���Activity��
		startActivityForResult(intent, 0);                        //��ǰɨ����������ά���,��ص���ǰ���onActivityResult����,
	}
	
    /**
     * ��дonActivityResult ����
     * ��ǰ��������startActivityForResult��ʽת��ҳ��,��Ŀ��ҳ������Ժ�,��ص��˷���
     * @param requestCode �ò�������  startActivityForResult(intent, 0); �еĵڶ�������ֵ
     * @param resultCode ��ת����Ŀ��ҳ����,setResult(RESULT_OK, intent); �еĵ�һ������ֵ
     * @param data ת����Intent����,���Դ�����ֵ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
        if(resultCode == RESULT_OK){                         //�жϻص�	
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");  //��ͻ�ȡ��ɨ���������
            Toast.makeText(search_activity.this, scanResult, Toast.LENGTH_SHORT).show();
            
            Bundle mBundle = new Bundle();
			mBundle.putBoolean("isByIsbn", true);
			mBundle.putString("isbn", scanResult);
			
			Intent mIntent = new Intent();
			mIntent.setClass(search_activity.this, bookDetail_activity.class);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
        }
    }
	
}
