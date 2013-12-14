package edu.wm.mashpotato;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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

	private boolean admin = true;// TODO GET RID OF AND CALL FOR THIS INFO

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_join_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
		}

		finalList = new ArrayList<String>();
		lv = (ListView) findViewById(R.id.listView1);
		instructions = (TextView) findViewById(R.id.instructions);
		maxPotatoes = (TextView) findViewById(R.id.numPotatoes);
		upgrades = (TextView) findViewById(R.id.upgrades);
		date = (TextView) findViewById(R.id.date);
		join_start_Button = (Button) findViewById(R.id.joinGame);

		updateInfo();

		if (admin) {
			join_start_Button.setText("Start the game.");
		} else if (inGame()) {
			join_start_Button.setText("Leave the game.");
		}

		// Load list view
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

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, finalList);
		lv.setAdapter(arrayAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Intent intent = new Intent(getApplicationContext(),
						GameJoinActivity.class);
				intent.putExtra("gameID", lv.getItemAtPosition(position)
						.toString());
				finish();
				startActivity(intent);
			}
		});

		join_start_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("start/join clicked");

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

				if (true) // TODO response = success
				{
					Intent intent = new Intent(getApplicationContext(),
							HomeScreenActivity.class);
					finish();
					startActivity(intent);
				} else {
					// TODO error msg
				}
			}
		});
	}

	private boolean inGame() {
		// TODO
		return false;
	}

	private void joinOrStartGame() {
		// TODO
		if (admin) {
			// start
		} else if (inGame()) {
			// leave
		} else {
			// join
		}

	}

	private void updateInfo() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO add call to get info for these fields.
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void loadLV() {
		HttpResponse response = null;
		HttpEntity responseEntity = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			// TODO LINK + creds
			request.setURI(new URI(
					"http://mighty-sea-1005.herokuapp.com/players/alive"));
			request.addHeader(BasicScheme.authenticate(
					new UsernamePasswordCredentials("brittany", "yes"),
					"UTF-8", false));
			response = client.execute(request);
			responseEntity = response.getEntity();

			String content = EntityUtils.toString(responseEntity);

			playerList = Arrays.asList(content.split("\\s*,\\s*"));

			for (int x = 0; x < playerList.size(); x++) {
				if (playerList.get(x).startsWith("\"userId\":")) {
					System.out.println(playerList.get(x));
					finalList.add(playerList.get(x).substring(10,
							playerList.get(x).length() - 1));
				} else {
					System.out.println("not " + playerList.get(x));
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
