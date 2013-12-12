package edu.wm.mashpotato;


import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.ResponseObject;
import edu.wm.mashpotato.web.WebTask;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText usernameText;
	private EditText passwordText;
	private Button registerButton;
	private Button loginButton;

	private UserLoginTask mAuthTask = null;

	private String username;
	private String password;
	private boolean isAdmin;
	
	private static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		usernameText = (EditText) findViewById(R.id.username);
		passwordText = (EditText) findViewById(R.id.password);

		registerButton = (Button) findViewById(R.id.createButton);
		loginButton = (Button) findViewById(R.id.loginButton);

		if (savedInstanceState == null) {
			username = "";
		} else {
			username = savedInstanceState.getString("username");
		}


		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("register");
				Intent intent = new Intent(getApplicationContext(),
						RegisterActivity.class);
				finish();
				startActivity(intent);
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("login");
				attemptLogin();
			}
		});
	}

	private void attemptLogin() {
		// Reset errors.
		usernameText.setError(null);
		passwordText.setError(null);

		// Store values at the time of the login attempt.
		username = usernameText.getText().toString();
		password = passwordText.getText().toString();

		boolean cancel = false;
		View focusView = null;
		Log.v(TAG, password);
		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			Log.v(TAG, "Empty password: " + password);
			passwordText.setError(getString(R.string.error_field_required));
			focusView = passwordText;
			cancel = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(username)) {
			usernameText.setError(getString(R.string.error_field_required));
			focusView = usernameText;
			cancel = true;
		}

		if (cancel) {
			// There was an error
			focusView.requestFocus();
		} else {
			mAuthTask = new UserLoginTask(false, usernameText.getText().toString(), passwordText.getText().toString(), null, false, false);
			mAuthTask.execute(new String[] {Constants.url});
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends WebTask {
		public UserLoginTask(boolean hasPairs, String username,
				String password, List<NameValuePair> pairs, boolean isPost, boolean lobby) {
			super(hasPairs, username, password, pairs, isPost, lobby);
		}


		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
			ResponseObject resp = new ResponseObject();
			resp.success = false;
			try {
				resp = ResponseObject.createResponse(result, this.lobby);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resp.success) {
				Intent intent = new Intent(getApplicationContext(),
						HomeScreenActivity.class);
				Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
				intent.putExtra("username", usernameText.getText()
						.toString());
				intent.putExtra("password", passwordText.getText()
						.toString());
				intent.putExtra("isAdmin", isAdmin);
				finish();
				startActivity(intent);
			} else {
				passwordText
						.setError(getString(R.string.error_incorrect_password));
				passwordText.requestFocus();
				Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

}
