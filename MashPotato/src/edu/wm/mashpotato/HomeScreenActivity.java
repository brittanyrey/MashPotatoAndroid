package edu.wm.mashpotato;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import edu.wm.mashpotato.accelerometer.SaveThePotatoActivity;
import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.Game;
import edu.wm.mashpotato.web.ResponseObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class HomeScreenActivity extends Activity {
	// implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	// NfcAdapter mNfcAdapter;
	// NfcManager mNfcManager;

	private static final int MESSAGE_SENT = 1;

	private ViewFlipper viewFlipper;
	private float lastX;

	// home pg
	private Button savePotato;
	private ImageButton stats;
	private Button logout;
	private ListView lv;

	// stats pg
	private TextView usernameText;
	private TextView score;
	private TextView hasPotato;
	private TextView status;
	private ImageView icon;
	private Button leaveGame;

	private String username;
	private String password;
	private Game gameObj;

	private ArrayList<String> finalList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
			gameObj = (Game) extras.get("gameObj");
		}

		finalList = new ArrayList<String>();
		viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		savePotato = (Button) findViewById(R.id.saveThePotato);
		lv = (ListView) findViewById(R.id.listView1);
		logout = (Button) findViewById(R.id.logout);
		stats = (ImageButton) findViewById(R.id.stats);

		usernameText = (TextView) findViewById(R.id.username);
		score = (TextView) findViewById(R.id.score);
		hasPotato = (TextView) findViewById(R.id.hasPotato);
		status = (TextView) findViewById(R.id.playerStatus);
		icon = (ImageView) findViewById(R.id.avatar);
		leaveGame = (Button) findViewById(R.id.leaveGame);

		setStats();
		loadLV();
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, finalList);
		lv.setAdapter(arrayAdapter);

		viewFlipper.showNext();
		stats.setBackgroundResource(R.drawable.info);
		stats.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY));

		/*
		 * mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 * 
		 * // Register callback to set NDEF message
		 * mNfcAdapter.setNdefPushMessageCallback(this, this); // Register
		 * callback to listen for message-sent success
		 * mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		 */
		// if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(
		// getIntent().getAction())) mNfcManager.processIntent(getIntent());

		leaveGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						leaveGame();
					}
				});
				thread.start();

				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		savePotato.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out
						.println("SAVE THE POTATO. DO IT FOR THE CHILDRENNNNNN!!!!");
				Intent intent = new Intent(getApplicationContext(),
						SaveThePotatoActivity.class);
				startActivity(intent);
			}
		});
		logout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("logout");
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				finish();
				startActivity(intent);
			}
		});
		stats.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("stats " + viewFlipper.getDisplayedChild());
				// 0 = stats. //1 = home
				if (viewFlipper.getDisplayedChild() == 1) {
					System.out.println("in top");
					viewFlipper.setInAnimation(getApplicationContext(),
							R.anim.in_from_left);
					viewFlipper.setOutAnimation(getApplicationContext(),
							R.anim.out_to_right);
					viewFlipper.showPrevious();
					stats.setBackgroundResource(R.drawable.home);
				} else {
					System.out.println("on botton");
					viewFlipper.setInAnimation(getApplicationContext(),
							R.anim.in_from_right);
					viewFlipper.setOutAnimation(getApplicationContext(),
							R.anim.out_to_left);
					viewFlipper.showNext();
					stats.setBackgroundResource(R.drawable.info);
				}
			}
		});
	}

	// TODO DOES NOT WORK
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
			
			ResponseObject resp = ResponseObject.createResponse(result, false, username);
			System.out.println("response: " + resp.success + " "+resp.game);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(getApplicationContext(),
				InitGameActivity.class);
		intent.putExtra("username", username
				.toString());
		intent.putExtra("password", password
				.toString());
		finish();
		startActivity(intent);
	}

	private void setStats() {
		for (int x = 0; x < gameObj.getPlayers().size(); x++) {
			if (gameObj.getPlayers().get(x).getId().equals(username)) {
				usernameText.setText(username);
				score.setText(gameObj.getPlayers().get(x).getScore() + "");
				hasPotato
						.setText(gameObj.getPlayers().get(x).getPotatoList() != null
								&& gameObj.getPlayers().get(x).getPotatoList()
										.size() > 0 ? "Yes" : "No");
				status.setText(gameObj.getPlayers().get(x).isOut() ? "Out"
						: "Alive");
				// TODO ADD BACK IN
				// //icon.setBackgroundResource(gameObj.getPlayers().get(x).getIcon());
			}
		}
	}

	private void loadLV() {

		for (int x = 0; x < gameObj.getPlayers().size(); x++) {
			finalList.add(gameObj.getPlayers().get(x).getId());
		}
	}

	// Method to handle touch event like left to right swap and right to left
	// swap
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();

			// if left to right swipe on screen
			if (lastX < currentX) {
				// If no more View/Child to flip
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen
				// will go OUT from Right
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				// Show the next Screen
				viewFlipper.showNext();
				stats.setBackgroundResource(R.drawable.home);
			}

			// if right to left swipe on screen
			if (lastX > currentX) {
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen
				// will go OUT from Left
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				// Show The Previous Screen
				viewFlipper.showPrevious();

				stats.setBackgroundResource(R.drawable.info);
			}
			break;
		}
		}
		return false;
	}

	/*
	 * @Override public void onNdefPushComplete(NfcEvent arg0) { // A handler is
	 * needed to send messages to the activity when this // callback occurs,
	 * because it happens from a binder thread
	 * mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	 * 
	 * }
	 * 
	 * @Override public NdefMessage createNdefMessage(NfcEvent arg0) { Time time
	 * = new Time(); time.setToNow(); String text = ("Beam me up!\n\n" +
	 * "Beam Time: " + time .format("%H:%M:%S")); NdefMessage msg = new
	 * NdefMessage(new NdefRecord[] { createMimeRecord(
	 * "application/edu.wm.mashpotato", text.getBytes())
	 *//**
	 * The Android Application Record (AAR) is commented out. When a device
	 * receives a push with an AAR in it, the application specified in the AAR
	 * is guaranteed to run. The AAR overrides the tag dispatch system. You can
	 * add it back in to guarantee that this activity starts when receiving a
	 * beamed message. For now, this code uses the tag dispatch system.
	 */
	/*
	 * // ,NdefRecord.createApplicationRecord("com.example.android.beam") });
	 * return msg; }
	 *//**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 * 
	 * @param mimeType
	 */
	/*
	 * public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
	 * byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
	 * NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
	 * mimeBytes, new byte[0], payload); return mimeRecord; }
	 *//** This handler receives a message from onNdefPushComplete */
	/*
	 * private final Handler mHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { switch (msg.what) {
	 * case MESSAGE_SENT: Toast.makeText(getApplicationContext(),
	 * "Potato passed!", Toast.LENGTH_LONG).show(); break; } } };
	 */

}
