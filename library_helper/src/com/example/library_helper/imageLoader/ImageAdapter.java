package com.example.library_helper.imageLoader;

import java.util.List;
import java.util.ResourceBundle.Control;

import org.objenesis.instantiator.basic.NewInstanceInstantiator;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.library_helper.R;
import com.example.library_helper.model.Book;

import android.app.Activity;
import android.content.Context;
import android.provider.Telephony.Sms.Conversations;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter implements OnScrollListener {

	private Context context;
	private List<Book> data;
	private static LayoutInflater inflater = null;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private Boolean scrolling;
	

	public ImageAdapter(Context c, List<Book> l) {
		// TODO Auto-generated constructor stub
		context = c;
		data = l;
		scrolling = false;
		inflater = LayoutInflater.from(context);
		mQueue = Volley.newRequestQueue(context);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());
	}
	
	public void setData(List<Book> books) {
		data = books;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Book book = data.get(position);
		
		//System.out.println("getView " + position + " " + convertView);
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			
			holder.book_title = (TextView) convertView.findViewById(R.id.book_title);
			holder.book_content = (TextView) convertView.findViewById(R.id.book_content);
			holder.cover = (NetworkImageView) convertView.findViewById(R.id.book_photo);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.cover.setImageUrl(null, imageLoader);
		}
		
		String title = book.getName();
		holder.book_title.setText(title);
		
		String content = "作者: " + book.getAuthor() + "   " + "出版社: " + book.getPublisher();
		//String content = book.getPic();
		holder.book_content.setText(content);
		
		String imgUrl = book.getPic();
		
		holder.cover.setImageResource(R.drawable.ic_launcher);
		if(imgUrl != null && !imgUrl.equals("")) {
			if(!scrolling) {
				ImageListener listener = ImageLoader.getImageListener(holder.cover,
						R.drawable.ic_launcher, R.drawable.ic_launcher);
				
				//Log.i("position", String.valueOf(position));
				//Log.i("url", imgUrl);
				
				//imageLoader.get(imgUrl, listener);
				holder.cover.setDefaultImageResId(R.drawable.ic_launcher);  
				holder.cover.setErrorImageResId(R.drawable.ic_launcher);  
				holder.cover.setImageUrl(imgUrl, imageLoader); 
			}

		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView book_title;
		TextView book_content;
		NetworkImageView cover;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {  
        case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
            // 手指触屏拉动准备滚动，只触发一次 
        	scrolling = true;
        	Log.i("scrolling", scrolling.toString());
            break;  
        case AbsListView.OnScrollListener.SCROLL_STATE_FLING:  
            // 持续滚动开始，只触发一次 
        	scrolling = true;
        	Log.i("scrolling", scrolling.toString());
            break;  
        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
            // 整个滚动事件结束，只触发一次  
        	scrolling = false;
        	Log.i("scrolling", scrolling.toString());
            break;  
        default:  
            break;  
    }  
	}
}
