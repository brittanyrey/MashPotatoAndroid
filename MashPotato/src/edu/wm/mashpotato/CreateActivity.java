package edu.wm.mashpotato;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class CreateActivity extends Activity {

	private String username = "userbase";
	private String password = "password";
	private String response = "fail";

	private Button newGameButton;
	private EditText numPotatoes;
	private CheckBox gems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
		}

		newGameButton = (Button) findViewById(R.id.newGame);
		numPotatoes = (EditText) findViewById(R.id.numPotatoes);
		gems = (CheckBox) findViewById(R.id.checkBox);

		newGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("new game clicked");

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO PUT BACK
						// createGame();
					}
				});
				thread.start();

				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (true) // TODO response = success
				{
					System.out.println("join a game");
					Intent intent = new Intent(getApplicationContext(),
							JoinActivity.class);
					finish();
					startActivity(intent);
				} else {
					// TODO error msg
				}
			}
		});
	}

	private void createGame() {
		// Create a new HttpClient and Post Header
		// TODO CHANGE LINKS
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://mighty-sea-1005.herokuapp.com/admin/newGame");
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"brittany", "yes");
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", username));
			nameValuePairs.add(new BasicNameValuePair("numPotatoes",
					numPotatoes.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("gems", String
					.valueOf(gems.isChecked())));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			response = httpresponse.getEntity().toString();

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		}
	}

}