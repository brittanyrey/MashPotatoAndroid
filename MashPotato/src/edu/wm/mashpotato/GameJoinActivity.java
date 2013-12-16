package edu.wm.mashpotato;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.Game;
import edu.wm.mashpotato.web.ResponseObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class GameJoinActivity extends Activity {

	private ListView lv;
	private TextView instructions;
	private TextView maxPotatoes;
	private TextView upgrades;
	private TextView date;
	private Button join_start_Button;

	private List<String> playerList;
	private ArrayList<String> finalList;
	private int pos;

	private String username;
	private String password;
	private Game gameObj;

	private ResponseObject resObj;
	private ResponseObject joinObj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_join_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
			gameObj = (Game) extras.get("gameObj");
		}

		lv = (ListView) findViewById(R.id.listView1);
		instructions = (TextView) findViewById(R.id.instructions);
		maxPotatoes = (TextView) findViewById(R.id.numPotatoes);
		date = (TextView) findViewById(R.id.date);
		join_start_Button = (Button) findViewById(R.id.joinGame);

		loadLV();
		updateInfo();

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, finalList);
		lv.setAdapter(arrayAdapter);

		join_start_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("start/join clicked");
				if (!gameObj.getOwner().equals(username)) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							joinOrStartGame();
						}
					});
					thread.start();

					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					loadLV();
					updateInfo();
					ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_list_item_1, finalList);
					lv.setAdapter(arrayAdapter);
				} else {
					if (gameObj.getPlayers().size() < 3){
						Toast.makeText(getApplicationContext(), "Games must have at least 3 players.",
								   Toast.LENGTH_LONG).show();
					}
					else{
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							joinOrStartGame();
						}
					});
					thread.start();

					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}}
				}
			}
		});
	}

	private boolean inGame() {
		for (int x = 0; x < gameObj.getPlayers().size(); x++) {
			if (gameObj.getPlayers().get(x).getId().equals(username)) {
				return true;
			}
		}
		return false;
	}

	private void joinOrStartGame() {
		if (gameObj.getOwner().equals(username)) {
			startGame();
		} else if (inGame()) {
			leaveGame();
		} else {
			joinGame();
		}
	}

	private void leaveGame() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.removePlayer);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("playerId", username));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity responseEntity = httpresponse.getEntity();

			String result = EntityUtils.toString(responseEntity);
			System.out.println(result);

			resObj = ResponseObject.createResponse(result, true, username);
			System.out.println("response: " + resObj.success + " "
					+ resObj.game);

			gameObj = resObj.lobbyList.get(0);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void startGame() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.startGame);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs
					.add(new BasicNameValuePair("gameID", gameObj.getId()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity responseEntity = httpresponse.getEntity();
			String content = EntityUtils.toString(responseEntity);

			resObj = ResponseObject.createResponse(content, false, username);
			System.out.println("start game: " + content);
			gameObj = resObj.game;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(getApplicationContext(),
				HomeScreenActivity.class);
		intent.putExtra("username", username.toString());
		intent.putExtra("password", password.toString());
		intent.putExtra("gameObj", gameObj);
		intent.putExtra("player", resObj.me);
		finish();
		startActivity(intent);
	}

	private void joinGame() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.joinGame);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, httppost);
			httppost.addHeader(authorizationHeader);

			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs
					.add(new BasicNameValuePair("gameID", gameObj.getId()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity responseEntity = httpresponse.getEntity();

			String content = EntityUtils.toString(responseEntity);

			resObj = ResponseObject.createResponse(content, true, username);
			gameObj = resObj.lobbyList.get(0);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateInfo() {
		if (gameObj.getOwner().equals(username)) {
			join_start_Button.setText("Start the game.");
		} else if (inGame()) {
			join_start_Button.setText("Leave the game.");
		} else {
			join_start_Button.setText("Join the game.");
		}
		date.setText(String.valueOf(gameObj.getCreationDate()));
	}

	private void loadLV() {
		finalList = new ArrayList<String>();
		for (int x = 0; x < gameObj.getPlayers().size(); x++) {
			finalList.add(gameObj.getPlayers().get(x).getId());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					loadJoinLV();
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
			intent.putExtra("gameObj", joinObj);
			finish();
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loadJoinLV() {
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

			joinObj = ResponseObject.createResponse(content, true, username);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
