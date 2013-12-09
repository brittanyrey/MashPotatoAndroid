package edu.wm.mashpotato;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText usernameText;
	private EditText passwordText;
	private Button registerButton;
	private Button loginButton;

	private UserLoginTask mAuthTask = null;

	private String username;
	private String password;
	private boolean isAdmin;

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

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
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
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

//			String hashedPassword = null;
//			try {
//				hashedPassword = Hasher.md5(password);
//				System.out.println("hashed password ==== " + hashedPassword);
//
//				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//				postParameters
//						.add(new BasicNameValuePair("user", username));
//
//				String response = null;
//
//				response = CustomHttpClient.executeHttpPost(
//						"http://mighty-sea-1005.herokuapp.com/login/",
//						postParameters);
//				String res = response.toString();
//				System.out.println(res);
//
//				List <String> resList = Arrays.asList(res.split(","));
//				System.out.println("to List "+resList.get(1).substring(1, resList.get(1).length() - 2));
//				
//				String resPWD = resList.get(0).substring(2, resList.get(0).length() - 1);
//				isAdmin = Boolean.parseBoolean(resList.get(1).substring(1, resList.get(1).length() - 3));
//				
//				System.out.println("retrieved password ==== " + resPWD + "\n isAdmin === " +isAdmin);
//
//				if (hashedPassword.equals(resPWD)) {
//					System.out.println("IT WORKS!!!");
//					return true;
//				}
//				else{
//					System.out.println("its broken...");
//					return false;
//				}
//			} catch (Exception e) {
//				System.out.println("it failed :( :( :((((((((((((((((((((((" + e.toString());
//			} 
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				Intent intent = new Intent(getApplicationContext(),
						HomeScreenActivity.class);
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
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

}
