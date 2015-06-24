package com.example.library_helper;

import com.example.library_helper.controller.DBSearchController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Intent mIntent;
	private Button loginbtn;
	private Button travelerbtn;
	private Button clearHisbtn;
	private TextView forgetbtn;
	private EditText stuid,password;
	private SharedPreferences sp ;
	private Context context = this;
	private CheckBox remember;
	private DBSearchController mController;
	String usernameValue;
	String passwordValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		loginbtn = (Button)findViewById(R.id.loginbtn);
		loginbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//if password is correct,login
				mIntent = new Intent();
				mIntent.setClass(MainActivity.this,search_activity.class);
				startActivity(mIntent);
				//if password is not correct,give a notice
				
				MainActivity.this.finish();
			}
		});
		
		travelerbtn = (Button)this.findViewById(R.id.travelerBtn);
		travelerbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIntent = new Intent();
				mIntent.setClass(MainActivity.this,search_activity.class);
				startActivity(mIntent);
				//if password is not correct,give a notice
				
				MainActivity.this.finish();
			}
		});
		
		mController = new DBSearchController(this.getApplicationContext());
		clearHisbtn = (Button)this.findViewById(R.id.clearHisBtn);
		clearHisbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//mController.clear("course");
				//mController.clear("bookname");
				mController.clearAll();
			}
		});
		
		forgetbtn = (TextView) findViewById(R.id.forgetbtn);
		forgetbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle("提示");
				builder.setMessage("帮助台邮箱helpdesk@mail.sysu.edu.cn，电话020-84036866（广州），0756-3668500（珠海）");
				builder.setPositiveButton("知道了", null);
				builder.show();
			}
		});
		sp = this.getSharedPreferences("studentid",context.MODE_PRIVATE );
		remember = (CheckBox)findViewById(R.id.checkBox1);
		stuid = (EditText) findViewById(R.id.stu_id);
		password = (EditText)findViewById(R.id.stu_passwosrd);
		usernameValue = stuid.getText().toString();
		passwordValue = password.getText().toString();
		System.out.println("1");
		if(remember.isChecked())
		{
			System.out.println("2");
			Editor editor = sp.edit();
			editor.putString("USER_NAME", usernameValue);
			editor.putString("PASSWORD", passwordValue); 
			editor.commit();
			System.out.println(usernameValue);
			System.out.println(passwordValue);
		}
		System.out.println("3");
		if(sp.getBoolean("ISCHECK", false))
		{
			remember.setChecked(true);
			stuid.setText(sp.getString("USER_NAME", ""));
			password.setText(sp.getString("PASSWORD", ""));
//			System.out.println(usernameValue);
//			System.out.println(passwordValue);
		}
		remember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(remember.isChecked())
				{
					sp.edit().putBoolean("ISCHECK", true).commit();
					System.out.println("checked");
				}
				else
				{
					sp.edit().putBoolean("ISCHECK", false).commit();
					System.out.println("not check");
				}
			}
		});

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
