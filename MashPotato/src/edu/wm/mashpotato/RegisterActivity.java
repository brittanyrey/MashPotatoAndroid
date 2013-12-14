package edu.wm.mashpotato;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.ResponseObject;
import edu.wm.mashpotato.web.WebTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	private EditText usernameText;
	private EditText passwordText;
	private EditText verifyPasswordText;
	// TODO MAKE VERIFY ACTUALLY ERROR CHECK OR DELETE IT
	private EditText firstNameText;
	private EditText lastNameText;
	private Button registerButton;
	private TextView text;

	private String username;
	private String password;
	private String hashedPassword;
	private String firstName;
	private String lastName;
	private String id;
	private String imageURL;
	private boolean clicked = false;

	ImageView selectedImage;

	 private Integer[] mImageIds = { R.drawable.cube, R.drawable.cone,
	 R.drawable.doublepyramid, R.drawable.pyramid };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);

		usernameText = (EditText) findViewById(R.id.username);
		passwordText = (EditText) findViewById(R.id.password);
		verifyPasswordText = (EditText) findViewById(R.id.verifyPassword);
		firstNameText = (EditText) findViewById(R.id.firstName);
		lastNameText = (EditText) findViewById(R.id.lastName);
		text = (TextView) findViewById(R.id.textVieww);
		registerButton = (Button) findViewById(R.id.createButton);

		text.setText("Choose an avatar.");

		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("set up");
				if (verifyPasswords() && !clicked){
					clicked = true;
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair("userName", usernameText
							.getText().toString()));
					pairs.add(new BasicNameValuePair("id", usernameText.getText()
							.toString()));
					pairs.add(new BasicNameValuePair("firstName", firstNameText
							.getText().toString()));
					pairs.add(new BasicNameValuePair("lastName", lastNameText
							.getText().toString()));
					pairs.add(new BasicNameValuePair("hashedPassword", passwordText
							.getText().toString()));
					AsyncTaskRunner runner = new AsyncTaskRunner(true, "admin",
							"admin", pairs, true, false);
					runner.execute(new String[] { Constants.addUser });
				}
			}
		});

		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		selectedImage = (ImageView) findViewById(R.id.imageView1);
		gallery.setSpacing(1);
		gallery.setAdapter(new GalleryImageAdapter(this));

		// clicklistener for Gallery
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				 imageURL = mImageIds[position].toString();
				 selectedImage.setImageResource(mImageIds[position]);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			finish();
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean verifyPasswords() {
		if (passwordText.getText() != verifyPasswordText.getText()) {
			System.out.println("passwords do not match ");
			verifyPasswordText.setError("Passwords do not match");
			return false;
		} 
		return true;
	}

	private class AsyncTaskRunner extends WebTask {

		public AsyncTaskRunner(boolean hasPairs, String username,
				String password, List<NameValuePair> pairs, boolean isPost,
				boolean lobby) {
			super(hasPairs, username, password, pairs, isPost, lobby);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			ResponseObject resp = new ResponseObject();
			resp.success = false;
			clicked = false;
			try {
				resp = ResponseObject.createResponse(result, this.lobby);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resp.success) {
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				finish();
				startActivity(intent);
			}
		}

	}

	public class GalleryImageAdapter extends BaseAdapter {
		private Context mContext;

		private Integer[] mImageIds = { R.drawable.cube, R.drawable.cone,
				R.drawable.doublepyramid, R.drawable.pyramid };

		public GalleryImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int index, View view, ViewGroup viewGroup) {
			ImageView i = new ImageView(mContext);

			i.setImageResource(mImageIds[index]);
			i.setLayoutParams(new Gallery.LayoutParams(200, 200));

			i.setScaleType(ImageView.ScaleType.FIT_XY);

			return i;
		}
	}

}
