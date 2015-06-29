package com.example.library_helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.library_helper.controller.SearchController;
import com.example.library_helper.imageLoader.ImageAdapter;
import com.example.library_helper.model.Book;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class result_activity extends Activity{
	private EditText keywordText;
	private RadioGroup group;
	private Button search_btn;
	private ListView resultlist;
	private List<Book> bookDatas;
	private int search_type = 0;
	private ImageAdapter imageAdapter;
	private Handler mHandler;
	private ProgressDialog mProgressDialog;
	ExecutorService executorService;
	private SearchController sc;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
		mHandler = new Handler() {
        	@Override
        	public void handleMessage(android.os.Message msg) {
        		
        		mProgressDialog.dismiss();

        		if(msg.what == 1) {
        			bookDatas = (List<Book>)msg.obj;
            		if(bookDatas != null && bookDatas.size() > 0) {
            			imageAdapter.setData(bookDatas);
            			imageAdapter.notifyDataSetChanged();
            		} else {
            			Toast.makeText(result_activity.this, "查询结果为空", Toast.LENGTH_SHORT).show();
            		}
        		} else if(msg.what == 2) {
        			Toast.makeText(result_activity.this, "请求超时", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 3) {
        			Toast.makeText(result_activity.this, "获取数据超时", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 4) {
        			Toast.makeText(result_activity.this, "网络错误", Toast.LENGTH_SHORT).show();
        		} else if(msg.what == 5) {
        			Toast.makeText(result_activity.this, "数据返回错误", Toast.LENGTH_SHORT).show();
        		} else {
        			Toast.makeText(result_activity.this, "未知错误", Toast.LENGTH_SHORT).show();
        		}
        	}
		};
		
		executorService = Executors.newSingleThreadExecutor();
		sc = new SearchController(this, mHandler);
		
		Bundle mBundle = this.getIntent().getExtras();
		String search_content = mBundle.getString("search_content");
		keywordText = (EditText)findViewById(R.id.keyword);
		if(search_content != null) {
			keywordText.setText(search_content);
		}
		
		String[] strBooks = mBundle.getStringArray("search_result");
		bookDatas = new ArrayList<Book>();
		for(String s: strBooks) {
			Book b = new Book();
			b.setByString(s);
			bookDatas.add(b);
		}
		
		/*for(Book b: bookDatas) {
			Log.i("book:name", b.getName());
			Log.i("book:pic", b.getPic());
			Log.i("book:author", b.getAuthor());
			Log.i("book:publisher", b.getPublisher());
			Log.i("book:isbn", b.getIsbn());
		}*/

		search_type = mBundle.getInt("search_type");
		
		group = (RadioGroup) findViewById(R.id.radioGroup);
		switch (search_type) {
		case 1:
			group.check(R.id.radioButton1);
			break;
		case 2:
			group.check(R.id.radioButton2);
			break;
		default:
			group.clearCheck();
			break;
		}
		
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1  == R.id.radioButton1)
				{
					//选择了课程
					//Toast.makeText(result_activity.this, "根据课程进行搜索", Toast.LENGTH_SHORT).show();
					search_type = 1;
				}
				else if(arg1 == R.id.radioButton2)
				{
					//选择了书名
					//Toast.makeText(result_activity.this, "根据书名进行搜索", Toast.LENGTH_SHORT).show();
					search_type = 2;
				} else {
					//error
					search_type = 0;
				}
			}
		});
		search_btn = (Button) findViewById(R.id.search_btn);
		search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//点击搜索按钮更新列表内容
				if(search_type == 1 || search_type == 2) {
					//search
					String sitem = keywordText.getText().toString().trim();
					if(sitem.equals("")) {
						Toast.makeText(result_activity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
					} else {
						//mProgressDialog = ProgressDialog.show(search_activity.this, "Requesting", "Requesting...");
						//Toast.makeText(result_activity.this, "开始查询", Toast.LENGTH_SHORT).show();
						sc.setSearchType(search_type);
						sc.setSearchItem(sitem);
						
						mProgressDialog = ProgressDialog.show(result_activity.this, "Requesting", "Requesting...");
						executorService.submit(sc);
					}
				} else {
					//error
					Toast.makeText(result_activity.this, "请选择查询方式", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		imageAdapter = new ImageAdapter(result_activity.this, bookDatas);
		resultlist = (ListView)this.findViewById(R.id.resultlist);
		Log.i("ttt", resultlist.toString());
		resultlist.setAdapter(imageAdapter);
		resultlist.setOnItemClickListener(mItemClickListener);
	}
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {			// TODO Auto-generated method stub
			
			Bundle mBundle = new Bundle();
			mBundle.putBoolean("isByIsbn", false);
			mBundle.putString("name", bookDatas.get(arg2).getName());
			mBundle.putString("author", bookDatas.get(arg2).getAuthor());
			mBundle.putString("pic", bookDatas.get(arg2).getPic());
			mBundle.putString("isbn", bookDatas.get(arg2).getIsbn());
			
			Intent mIntent = new Intent();
			mIntent.setClass(result_activity.this, bookDetail_activity.class);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
			
		}
		
	};
}
