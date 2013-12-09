package edu.wm.mashpotato;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
	//TODO MAKE VERIFY ACTUALLY ERROR CHECK OR DELETE IT
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

	ImageView selectedImage;
//	private Integer[] mImageIds = { R.drawable.cube, R.drawable.cone,
//			R.drawable.doublepyramid, R.drawable.pyramid };

	//TODO get user images ^^
	
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

		if (savedInstanceState == null) {
			username = "";
		} else {
			username = savedInstanceState.getString("username");
		}

		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("set up");
				AsyncTaskRunner runner = new AsyncTaskRunner();
				runner.execute();
				// saveUser(v);
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				finish();
				startActivity(intent);

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
				//TODO
//				imageURL = mImageIds[position].toString();
//				selectedImage.setImageResource(mImageIds[position]);
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

	private void saveUser(View v) {
		// this is for error handling
		/*
		 * if ( usernameText.getText().toString() == null ||
		 * passwordText.getText().toString() == "" ||
		 * verifyPasswordText.getText().toString() == "" ||
		 * firstNameText.getText().toString() == "" ||
		 * lastNameText.getText().toString() == "") {
		 * System.out.println("a field is blank");
		 * usernameText.setError("Your error message"); } else if
		 * (passwordText.getText() != verifyPasswordText.getText()) {
		 * System.out.println("passwords do not match ");
		 * System.out.println(lastNameText.getText().toString() == "");
		 * System.out.println(lastNameText.getText().toString() == null);
		 * System.out.println(lastNameText.getText() == null);
		 * 
		 * verifyPasswordText.setError("Passwords do not match"); } else{ }
		 */
	}

	private class AsyncTaskRunner extends AsyncTask<String, String, String> {

		private String resp;

		@Override
		protected String doInBackground(String... params) {
			publishProgress("Sleeping..."); // Calls onProgressUpdate()

			username = usernameText.getText().toString();
			firstName = firstNameText.getText().toString();
			lastName = lastNameText.getText().toString();
			id = usernameText.getText().toString();
			try {
//				hashedPassword = Hasher.md5(passwordText.getText().toString());
//				System.out.println(hashedPassword);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String data = "";
			try {
				data = URLEncoder.encode("username", "UTF-8") + "="
						+ URLEncoder.encode(username, "UTF-8");
				data += "&" + URLEncoder.encode("firstName", "UTF-8") + "="
						+ URLEncoder.encode(firstName, "UTF-8");

				data += "&" + URLEncoder.encode("lastName", "UTF-8") + "="
						+ URLEncoder.encode(lastName, "UTF-8");

				data += "&" + URLEncoder.encode("id", "UTF-8") + "="
						+ URLEncoder.encode(id, "UTF-8");

				data += "&" + URLEncoder.encode("hashedPassword", "UTF-8")
						+ "=" + URLEncoder.encode(hashedPassword, "UTF-8");

				data += "&" + URLEncoder.encode("imageURL", "UTF-8") + "="
						+ URLEncoder.encode(imageURL, "UTF-8");

				data += "&" + URLEncoder.encode("isAdmin", "UTF-8") + "="
						+ URLEncoder.encode("false", "UTF-8");

				System.out.println(data);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String text = "";
			BufferedReader reader = null;

			try {

				URL url = new URL(
						"http://mighty-sea-1005.herokuapp.com/addUser/");

				// Send POST data request

				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response

				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "\n");
				}

				text = sb.toString();
			} catch (Exception ex) {

			} finally {
				try {

					reader.close();
				}

				catch (Exception ex) {
				}
			}

			// Show response on activity
			System.out.println(text);

			return resp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			// finalResult.setText(result);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... text) {
			// finalResult.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}

	public class GalleryImageAdapter extends BaseAdapter 
	{
	    private Context mContext;

//	    private Integer[] mImageIds = { R.drawable.cube, R.drawable.cone,
//				R.drawable.doublepyramid, R.drawable.pyramid };
//TODO
	    
	    
	    public GalleryImageAdapter(Context context) 
	    {
	        mContext = context;
	    }

	    public int getCount() {
	        return 0; //TODO
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }


	   @Override
	    public View getView(int index, View view, ViewGroup viewGroup) 
	    {
	        // TODO 
	        ImageView i = new ImageView(mContext);

	       //i.setImageResource(mImageIds[index]);
	        i.setLayoutParams(new Gallery.LayoutParams(200, 200));
	    
	        i.setScaleType(ImageView.ScaleType.FIT_XY);

	        return i;
	    }
	}

	
}
