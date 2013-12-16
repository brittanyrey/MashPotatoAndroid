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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JoinActivity extends Activity {

	private ListView lv;
	private TextView instructions;

	private List<String> playerList;
	private ArrayList<String> finalList;
	private int pos;

	private String username;
	private String password;

	private ResponseObject resObj;
	private HttpResponse response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
			resObj = (ResponseObject) extras.get("gameObj");
		}

		finalList = new ArrayList<String>();
		lv = (ListView) findViewById(R.id.listView1);
		instructions = (TextView) findViewById(R.id.instructions);

		initialLoadLV();

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, finalList);
		lv.setAdapter(arrayAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Game gameObj = findGame(lv.getItemAtPosition(position)
						.toString());
				Intent intent = new Intent(getApplicationContext(),
						GameJoinActivity.class);
				intent.putExtra("gameObj", gameObj);
				intent.putExtra("username", username.toString());
				intent.putExtra("password", password.toString());
				finish();
				startActivity(intent);
			}
		});
	}

	private void initialLoadLV() {
		for (int x = 0; x < resObj.lobbyList.size(); x++) {
			finalList.add(resObj.lobbyList.get(x).getId());
		}

	}

	private Game findGame(String id) {
		for (int x = 0; x < resObj.lobbyList.size(); x++) {
			if (resObj.lobbyList.get(x).getId().equals(id)) {
				return resObj.lobbyList.get(x);
			}
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(),
					InitGameActivity.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			finish();
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
