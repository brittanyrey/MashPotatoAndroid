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

import edu.wm.mashpotato.web.Constants;

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
	private EditText maxRoundLength;
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
		maxRoundLength = (EditText) findViewById(R.id.maxRoundTime);

		newGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("new game clicked");

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						 createGame();
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
					intent.putExtra("username", username);
					intent.putExtra("password", password);
					finish();
					startActivity(intent);
				} else {
					// TODO error msg
				}
			}
		});
	}

	private void createGame() {
		System.out.println(username +" "+ password);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.newGame);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String time = String.valueOf(Integer.parseInt(maxRoundLength.getText().toString()) * 60000);
			nameValuePairs.add(new BasicNameValuePair("lifeSpan",
					time));
			nameValuePairs.add(new BasicNameValuePair("lat", "0"));
			nameValuePairs.add(new BasicNameValuePair("lng", "0"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			response = httpresponse.getEntity().toString();
			System.out.println(response);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		}
	}

}