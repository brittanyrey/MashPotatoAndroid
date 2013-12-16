package edu.wm.mashpotato;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.ResponseObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitGameActivity extends Activity {

	private Button createGameButton;
	private Button joinButton;
	private Button logoutButton;

	private String username;
	private String password;
	private String response;

	private ResponseObject resObj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_game_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
		}

		createGameButton = (Button) findViewById(R.id.createAGame);
		joinButton = (Button) findViewById(R.id.joinGame);
		logoutButton = (Button) findViewById(R.id.logout);

		createGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("create a game");

				 Intent intent = new Intent(getApplicationContext(),
				 CreateActivity.class);
				 intent.putExtra("username", username.toString());
				 intent.putExtra("password", password.toString());
				 finish();
				 startActivity(intent);
			}
		});

		joinButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("join a game " + username);

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						loadLV();
					}
				});
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(getApplicationContext(),
						JoinActivity.class);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("gameObj", resObj);
				finish();
				startActivity(intent);
			}
		});

		logoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("logout");
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				finish();
				startActivity(intent);
			}
		});
	}

	private void loadLV() {
		System.out.println(username);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.gameLobby);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("lat", "0"));
			nameValuePairs.add(new BasicNameValuePair("lng", "0"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity responseEntity = httpresponse.getEntity();

			String content = EntityUtils.toString(responseEntity);

			resObj = ResponseObject.createResponse(content, true, username);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
