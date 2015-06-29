package com.example.library_helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DateSerializer;
import com.example.library_helper.controller.GetBookDetailController;
import com.example.library_helper.controller.SearchController;
import com.example.library_helper.imageLoader.BitmapCache;
import com.example.library_helper.imageLoader.ImageAdapter;
import com.example.library_helper.model.Book;
import com.example.library_helper.model.BookDetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class bookDetail_activity extends Activity {
	
	ExecutorService executorService;
	private ProgressDialog mProgressDialog;
	private Handler mHandler;
	private GetDetailTask task;
	private BookDetail detail;
	private Button appointmentBtn;
	private Button backBtn;
	private TextView bookNameTextView;
	private TextView authorTextView;
	private TextView publisherTextView;
	private TextView isbnTextView;
	private TextView languageTextView;
	private TextView digestTextView;
	private TextView sysNumberTextView;
	private TextView bookStatesTextView;
	private ImageView bookCoverImageView;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private String appointment_url = "http://172.18.187.107:8005/lib/appointment/?";
	private SharedPreferences sp;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_detail);
		
		detail = null;
		
		executorService = Executors.newSingleThreadExecutor();
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		
		bookNameTextView = (TextView)this.findViewById(R.id.detail_book_name);
		authorTextView = (TextView)this.findViewById(R.id.detail_author);
		publisherTextView = (TextView)this.findViewById(R.id.detail_publisher);
		isbnTextView = (TextView)this.findViewById(R.id.detail_isbn);
		languageTextView = (TextView)this.findViewById(R.id.detail_language);
		sysNumberTextView = (TextView)this.findViewById(R.id.detail_sysNumber);
		bookStatesTextView = (TextView)this.findViewById(R.id.detail_bookStates);
		digestTextView = (TextView)this.findViewById(R.id.detail_digest);
		bookCoverImageView = (ImageView)this.findViewById(R.id.detail_book_cover);
		
		mQueue = Volley.newRequestQueue(bookDetail_activity.this);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());
		
		final Bundle mBundle = this.getIntent().getExtras();
		Boolean _isByIsbn = mBundle.getBoolean("isByIsbn");
		String _bname = null;
		String _author = null;
		String _pic = null;
		String _isbn = null;
		
		if(_isByIsbn) {
			_isbn = mBundle.getString("isbn");
		} else {
			_bname = mBundle.getString("name");
			_author = mBundle.getString("author");
			_pic = mBundle.getString("pic");
			_isbn = mBundle.getString("isbn");
		}
		
		mHandler = new Handler() {
        	@Override
        	public void handleMessage(android.os.Message msg) {
        		
        		mProgressDialog.dismiss();

        		if(msg.what == 1) {
        			detail = (BookDetail)msg.obj;
            		if(detail != null) {
            			if(mBundle.getString("pic") != null && !mBundle.getString("pic").equals("")) {
            				detail.setPic(mBundle.getString("pic"));
            			}
            			String bn = detail.getName();
            			String au = detail.getAuthor();
            			String pub = detail.getPublisher();
            			String i = detail.getIsbn();
            			String lang = detail.getLanguage();
            			String sysNum = detail.getSysNumber();
            			String d = detail.getDigest();
            			
            			bookNameTextView.setText(bn);
            			authorTextView.setText("著者:" + au);
            			publisherTextView.setText("出版社:" + pub);
            			isbnTextView.setText("ISBN:" + i);
            			languageTextView.setText("语种:" + lang);
            			sysNumberTextView.setText("分类号:" + sysNum);
            			
            			digestTextView.setText("摘要:\n" + d);
            			
            			List<Map<String, String>> bookstate = detail.getBookStates();
            			
            			String states = "";
            			if(bookstate != null) {
            				for(Map<String, String> state: bookstate) {
            					if(state != null) {
            						System.out.println(state.get("place") + "::" + state.get("bookPosition") + "::" + state.get("BorrowDate"));
            						states = states + state.get("place") + "::" + state.get("bookPosition") + "::" + state.get("BorrowDate") + "\n";
            					}
            				}
            			}
            			
            			bookStatesTextView.setText("馆藏信息:\n" + states);
            			
            			
            			
            			String imgUrl = detail.getPic();
            			//Toast.makeText(bookDetail_activity.this, imgUrl, Toast.LENGTH_SHORT).show();
            			if(imgUrl != null && !imgUrl.equals("")) {
            				ImageListener listener = ImageLoader.getImageListener(bookCoverImageView,
        							R.drawable.ic_launcher, R.drawable.ic_launch);
        					imageLoader.get(imgUrl, listener);            					
 
            			} else {
            				bookCoverImageView.setImageResource(R.drawable.ic_launcher);
            			}
            			
            		} else {
            			Toast.makeText(bookDetail_activity.this, "查询结果为空", Toast.LENGTH_SHORT).show();
            			bookDetail_activity.this.finish();
            		}
        		} else if(msg.what == 2) {
        			Toast.makeText(bookDetail_activity.this, "请求超时", Toast.LENGTH_SHORT).show();
        			bookDetail_activity.this.finish();
        		} else if(msg.what == 3) {
        			Toast.makeText(bookDetail_activity.this, "获取数据超时", Toast.LENGTH_SHORT).show();
        			bookDetail_activity.this.finish();
        		} else if(msg.what == 4) {
        			Toast.makeText(bookDetail_activity.this, "网络错误", Toast.LENGTH_SHORT).show();
        			bookDetail_activity.this.finish();
        		} else if(msg.what == 5) {
        			Toast.makeText(bookDetail_activity.this, "数据返回错误", Toast.LENGTH_SHORT).show();
        			bookDetail_activity.this.finish();
        		} else {
        			Toast.makeText(bookDetail_activity.this, "未知错误", Toast.LENGTH_SHORT).show();
        			bookDetail_activity.this.finish();
        		}
        	}
		};
		
		appointmentBtn = (Button) this.findViewById(R.id.detail_button1);
		appointmentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAppointmentDialog();
			}
		});
		
		backBtn = (Button) this.findViewById(R.id.detail_button2);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    bookDetail_activity.this.finish();	
			}
		});
		
		
		task = new GetDetailTask(mHandler);
		
		task.setIsByIsbn(_isByIsbn);
		if(_isByIsbn) {
			task.setIsbn(_isbn);
		} else {
			task.setBName(_bname);
			task.setAuthor(_author);
			task.setIsbn(_isbn);
		}
		
		mProgressDialog = ProgressDialog.show(bookDetail_activity.this, "Requesting", "Requesting...");
		executorService.submit(task);

	}
	
	private class GetDetailTask implements Runnable {
		
		private Handler handler;
		private GetBookDetailController gbdc;
		private String bname;
		private String author;
		private String isbn;
		private Boolean isByIsbn;
		
		public GetDetailTask(Handler h) {
			// TODO Auto-generated constructor stub
			handler = h;
			gbdc = new GetBookDetailController();
		}
		
		public void setBName(String _bname) {
			bname = _bname;
		}
		
		public void setAuthor(String _author) {
			author = _author;
		}
		
		public void setIsbn(String _isbn) {
			isbn = _isbn;
		}
		
		public void setIsByIsbn(Boolean _isByIsbn) {
			isByIsbn = _isByIsbn;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			Map<String, BookDetail> bookdetailResult = null;
			if(isByIsbn) {
				bookdetailResult = gbdc.getBookDetailByIsbn(isbn);
			} else {
				bookdetailResult = gbdc.getBookDetail(bname, author, isbn);
			}
			
		    String status_code = "-1";
		    BookDetail bookdetail = null;
			for (java.util.Map.Entry<String, BookDetail> entry: bookdetailResult.entrySet()) {
				status_code = entry.getKey();
				bookdetail = entry.getValue();
			}
			
			if(!(status_code != null && bookdetail != null)) {
				status_code = "-1";
				bookdetail = null;
			}
			
			handler.obtainMessage(Integer.parseInt(status_code), bookdetail).sendToTarget();
		}
		
	}
	
	private void showAppointmentDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(bookDetail_activity.this);
		final View view = View.inflate(bookDetail_activity.this, R.layout.appointment_dialog, null);
		
		List<Map<String, String>> bookstateItems = detail.getBookStates();
		List<String> bookstateSpinnerItems = new ArrayList<String>();
		final Map<String, Map<String, String> > appointmentItems1 = new HashMap<String, Map<String,String>>();
		
		if(bookstateItems != null) {
			for(Map<String, String> state: bookstateItems) {
				if(state != null && state.get("available") != null && state.get("available").equals("Y")) {
					String str = state.get("place") + "::" + state.get("bookPosition") + "::" + state.get("BorrowDate");
					bookstateSpinnerItems.add(str);
					Map<String, String> item = new HashMap<String, String>();
					item.put("doc_number", state.get("doc_number"));
					item.put("item_sequence", state.get("item_sequence"));
					appointmentItems1.put(str, item);
				}
			}
		}
		
		if(bookstateSpinnerItems.size() == 0) {
			bookstateSpinnerItems.add("暂时没有可预约图书");
		}

		final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner1);
		ArrayAdapter<String> arr_adapter1 = new ArrayAdapter<String>(bookDetail_activity.this,
				android.R.layout.simple_spinner_dropdown_item, bookstateSpinnerItems);
		spinner1.setAdapter(arr_adapter1);
		
		
		List<String> spinner2ItemList = new ArrayList<String>();
		final Map<String, String> appointmentItems2 = new HashMap<String, String>();
		spinner2ItemList.add("北校区流通");
		appointmentItems2.put("北校区流通", "BXLT");
		
		spinner2ItemList.add("南校区1楼总服务台");
		appointmentItems2.put("南校区1楼总服务台", "NXLT");
		
		spinner2ItemList.add("东校区流通");
		appointmentItems2.put("东校区流通", "DXLT");
		
		spinner2ItemList.add("珠海校区流通");
		appointmentItems2.put("珠海校区流通", "ZXLT");
		
		final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
		ArrayAdapter<String> arr_adapter2 = new ArrayAdapter<String>(bookDetail_activity.this,
				android.R.layout.simple_spinner_dropdown_item, spinner2ItemList);
		spinner2.setAdapter(arr_adapter2);
		
		final EditText dateEditText = (EditText) view.findViewById(R.id.dialog_date);
		final Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateString = sdf.format(calendar.getTime());
		dateEditText.setText(dateString);
		
		
		dateEditText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					//当点击DatePickerDialog控件的设置按钮时，调用该方法
				    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
				    {
				        @Override
				        public void onDateSet(DatePicker view, int year, int monthOfYear,
				                int dayOfMonth) {
				            //修改日历控件的年，月，日
				            //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
				            calendar.set(Calendar.YEAR, year);
				            calendar.set(Calendar.MONTH, monthOfYear);
				            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);   
				            //将页面TextView的显示更新为最新时间
				    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				    		String dateString = sdf.format(calendar.getTime());
				    		dateEditText.setText(dateString);
				        }       
				    };
				    
				    DatePickerDialog datePickerDialog = new DatePickerDialog(bookDetail_activity.this,
				    		d,
				    		calendar.get(Calendar.YEAR),
				    		calendar.get(Calendar.MONTH),
				    		calendar.get(Calendar.DAY_OF_MONTH));
				    datePickerDialog.show();
				}
				
				return false;
			}
		});
		
		final EditText sidEditText = (EditText)view.findViewById(R.id.dialog_studentid);
		final EditText passwordEditText = (EditText)view.findViewById(R.id.dialog_password);
		
		final CheckBox rem_pw = (CheckBox) view.findViewById(R.id.dialog_remember);
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(!isChecked) {
					sp.edit().putBoolean("ISCHECK", false).commit();
				}
			}
			
		});
		
		if(sp.getBoolean("ISCHECK", false)) {
			rem_pw.setChecked(true);
			sidEditText.setText(sp.getString("SID", ""));
			passwordEditText.setText(sp.getString("PASSWORD", ""));
		}
		
		builder.setTitle("预约请求");
		builder.setView(view);
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
        builder.setPositiveButton("预约", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				final String username = sidEditText.getText().toString();
				
				final String password = passwordEditText.getText().toString();
				
				Map<String, String> item1 = appointmentItems1.get(spinner1.getSelectedItem().toString());
				
				String pickup = appointmentItems2.get(spinner2.getSelectedItem().toString());
				
				String endtime = dateEditText.getText().toString();
				
				if(username != null && !username.equals("") && password != null && !password.equals("")
						&& item1 != null & pickup != null && !pickup.equals("") && endtime != null
						&& !endtime.equals("")) {
					String doc_number = item1.get("doc_number");
					String item_sequence = item1.get("item_sequence");
					if(doc_number != null && !doc_number.equals("") && item_sequence != null && !item_sequence.equals("")) {
						String cur_url = appointment_url;
						try {
							cur_url = cur_url + "username=" + URLEncoder.encode(username, "utf-8")  + "&"
							    + "password=" + URLEncoder.encode(password, "utf-8") + "&"
							    + "doc_number=" + URLEncoder.encode(doc_number, "utf-8") + "&"
							    + "item_sequence=" + URLEncoder.encode(item_sequence, "utf-8") + "&"
							    + "pickup=" + URLEncoder.encode(pickup, "utf-8") + "&"
							    + "end_time=" + URLEncoder.encode(endtime, "utf-8");
							
							StringRequest stringRequest = new StringRequest(cur_url,
									new Response.Listener<String>() {
								        @Override
								        public void onResponse(String response) {
								        	int pos1 = 0;
								        	if((pos1 = response.indexOf(":")) != -1) {
								        		String status = response.substring(0, pos1);
								        		if(status.equals("Success")) {
								        			if(rem_pw.isChecked()) {
								        				Editor editor = sp.edit();
								        				editor.putBoolean("ISCHECK", true);
								        				editor.putString("SID", username);
								        				editor.putString("PASSWORD", password);
								        				editor.commit();
								        			} else {
								        				Editor editor = sp.edit();
								        				editor.putBoolean("ISCHECK", false);
								        				editor.putString("SID", "");
								        				editor.putString("PASSWORD", "");
								        				editor.commit();
								        			}
								        		} else {
								        			if(!rem_pw.isChecked()) {
								        				Editor editor = sp.edit();
								        				editor.putBoolean("ISCHECK", false);
								        				editor.commit();
								        			}
								        		}
								        		Toast.makeText(bookDetail_activity.this, response, Toast.LENGTH_SHORT).show();
								        	} else {
								        		Toast.makeText(bookDetail_activity.this, "error", Toast.LENGTH_SHORT).show();
								        	}
								        }
									}, new Response.ErrorListener() {
										 @Override  
				                          public void onErrorResponse(VolleyError error) {  
				                              Log.e("TAG", error.getMessage(), error);
				                              Toast.makeText(bookDetail_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
				                          }
									});
						     
							mQueue.add(stringRequest);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(bookDetail_activity.this, "未知错误", Toast.LENGTH_SHORT).show();
						}
						
						Log.i("appointment_url", cur_url);
					} else {
						Toast.makeText(bookDetail_activity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(bookDetail_activity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		builder.create().show();
		
	}

}
