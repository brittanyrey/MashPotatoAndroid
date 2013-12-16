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
import edu.wm.mashpotato.web.Player;
import edu.wm.mashpotato.web.ResponseObject;
import edu.wm.mashpotato.web.WebTask;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;
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

public class HomeScreenActivity extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback, LocationListener {
	NfcAdapter mNfcAdapter;
	// NfcManager mNfcManager;

	private static final int MESSAGE_SENT = 1;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	LocationManager mlocManager;
	private boolean canGetLocation;

	private static final String TAG = "HomeScreenActivity";

	private ViewFlipper viewFlipper;
	private float lastX;

	// home pg
	private Button savePotato;
	private ImageButton stats;
	private ImageButton logout;
	private ListView lv;

	// stats pg
	private TextView usernameText;
	private TextView score;
	private TextView hasPotato;
	private TextView status;
	private ImageView icon;
	private Button leaveGame;

	private static String username;
	private static String password;
	private static Game gameObj;

	private ArrayList<String> finalList;

	private IntentFilter[] intentFiltersArray;
	private Activity why;
	private IntentFilter ndef;
	private PendingIntent pendingIntent;

	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		why = this;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			password = extras.getString("password");
			gameObj = (Game) extras.get("gameObj");
			player = (Player) extras.get("player");
		}
		Log.i(TAG, "OnNewIntent: " + username + " password: " + password
				+ " gameObj: " + gameObj);

		finalList = new ArrayList<String>();
		viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		savePotato = (Button) findViewById(R.id.saveThePotato);
		lv = (ListView) findViewById(R.id.listView1);
		logout = (ImageButton) findViewById(R.id.logout);
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
		stats.setColorFilter(new PorterDuffColorFilter(Color.GREEN,
				PorterDuff.Mode.MULTIPLY));
		logout.setBackgroundResource(R.drawable.power);
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener mlocListener = this;
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME_BW_UPDATES, 0, mlocListener);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (player.isHasString()) {
			mNfcAdapter.setNdefPushMessageCallback(this, this);
			mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
			Log.i(TAG, "I have a potato!");
		} else {
			mNfcAdapter.setNdefPushMessageCallback(null, this);
			Log.i(TAG, "No potato!");
		}
		// mNfcAdapter.disableForegroundNdefPush(this);
		// Register callback to listen for message-sent success

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*"); /*
									 * Handles all MIME based dispatches. You
									 * should specify only the ones that you
									 * need.
									 */
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		intentFiltersArray = new IntentFilter[] { ndef, };

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
				intent.putExtra("username", username);
		    	intent.putExtra("password", password);
		    	intent.putExtra("gameObj", gameObj);
		    	intent.putExtra("player", player);
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
		mHandler.postDelayed(dynamicPoll, 30000);
	}

	private void leaveGame() {
		System.out.println(username);
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

			ResponseObject resp = ResponseObject.createResponse(result, false,
					username);
			System.out.println("response: " + resp.success + " " + resp.game);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setStats() {
		for (int x = 0; x < gameObj.getPlayers().size(); x++) {
			if (gameObj.getPlayers().get(x).getId().equals(username)) {
				usernameText.setText(username);
				score.setText(gameObj.getPlayers().get(x).getScore() + "");
				hasPotato
						.setText(gameObj.getPlayers().get(x).isHasString() ? "Yes"
								: "No");
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

	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Time time = new Time();
		time.setToNow();
		String text = ("" + player.getId() + " " + gameObj.getPotato().get(0)
				.getpId());
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
				"application/edu.wm.mashpotato", text.getBytes())

		/**
		 * The Android Application Record (AAR) is commented out. When a device
		 * receives a push with an AAR in it, the application specified in the
		 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
		 * You can add it back in to guarantee that this activity starts when
		 * receiving a beamed message. For now, this code uses the tag dispatch
		 * system.
		 */
		// ,NdefRecord.createApplicationRecord("com.example.android.beam")
				});
		return msg;
	}

	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				mNfcAdapter.setNdefPushMessageCallback(null, why);
				Log.i(TAG, "No potato!");
				player.setHasString(false);
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair(Constants.potatoId, ""));
				pairs.add(new BasicNameValuePair(Constants.holder, usernameText
						.getText().toString()));
				pairs.add(new BasicNameValuePair(Constants.score, player
						.getScore() + ""));
				pairs.add(new BasicNameValuePair(Constants.temp, 0 + ""));
				pairs.add(new BasicNameValuePair(Constants.lat, player.getLat()
						+ ""));
				pairs.add(new BasicNameValuePair(Constants.lng, player.getLng()
						+ ""));

				UserLoginTask task = new UserLoginTask(true, username,
						password, pairs, true, false);
				task.execute(Constants.updatePlayerInfo);
				Toast.makeText(getApplicationContext(), "Potato passed!",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	/**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 * 
	 * @param mimeType
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public void onResume() {
		super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
				intentFiltersArray, null);

		Log.i(TAG, "OnResume: " + username + " password: " + password
				+ " gameObj: " + gameObj);
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
		Log.i(TAG, "ONPUASE " + username + " password: " + password
				+ " gameObj: " + gameObj);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		Log.i(TAG, "SAVEDINSTANCESTATE: " + username + " password: " + password
				+ " gameObj: " + gameObj);

		savedInstanceState.putString("username", username);
		savedInstanceState.putString("password", password);
		savedInstanceState.putSerializable("gameObj", gameObj);
		savedInstanceState.putSerializable("player", player);
		// etc.
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		Log.i(TAG, "OnNewIntent: " + username + " password: " + password
				+ " gameObj: " + gameObj);

		intent.putExtra("username", username);
		intent.putExtra("password", password);
		intent.putExtra("gameObj", gameObj);
		intent.putExtra("player", player);
		setIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		Toast.makeText(getApplicationContext(),
				new String(msg.getRecords()[0].getPayload()),
				Toast.LENGTH_SHORT).show();
		// mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
		String results = new String(msg.getRecords()[0].getPayload());
		String[] arr = results.split(" ");
		player.setHasString(true);
		List<String> potatoList = new ArrayList<String>();
		potatoList.add(arr[1]);
		player.setPotatoList(potatoList);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair(Constants.potatoId, arr[1]));
		pairs.add(new BasicNameValuePair(Constants.holder, arr[0]));
		pairs.add(new BasicNameValuePair(Constants.score, player.getScore()
				+ ""));
		pairs.add(new BasicNameValuePair(Constants.temp, 0 + ""));
		pairs.add(new BasicNameValuePair(Constants.lat, player.getLat() + ""));
		pairs.add(new BasicNameValuePair(Constants.lng, player.getLng() + ""));

		UserLoginTask task = new UserLoginTask(true, username, password, pairs,
				true, false);
		task.execute(Constants.updatePlayerInfo);
		Toast.makeText(getApplicationContext(), "Potato passed!",
				Toast.LENGTH_LONG).show();
	}

	public class UserLoginTask extends WebTask {
		public UserLoginTask(boolean hasPairs, String username,
				String password, List<NameValuePair> pairs, boolean isPost,
				boolean lobby) {
			super(hasPairs, username, password, pairs, isPost, lobby);
		}

		@Override
		protected void onPostExecute(String result) {
			ResponseObject resp = new ResponseObject();
			resp.success = false;
			try {
				resp = ResponseObject.createResponse(result, this.lobby,
						usernameText.getText().toString());
				System.out.println("response: " + resp.success + " "
						+ resp.game);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resp.success) {
				Intent intent = null;
				intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
				gameObj = resp.game;
				player = resp.me;
				intent.putExtra(Constants.response, resp);
				Toast.makeText(getApplicationContext(), "Success!",
						Toast.LENGTH_SHORT).show();
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("gameObj", gameObj);
				intent.putExtra("player", player);
				finish();
				startActivity(intent);
			}
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		player.lat = loc.getLatitude();
		player.lng = loc.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Disabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public Location getLocation() {
		Location location = null;
		// Log.e(TAG, "Get Location");
		try {

			// getting GPS status
			boolean isGPSEnabled = mlocManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = mlocManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					mlocManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					// Log.d("Network", "Network");
					if (mlocManager != null) {
						location = mlocManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							player.lat = location.getLatitude();
							player.lng = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						mlocManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						// Log.d("GPS Enabled", "GPS Enabled");
						if (mlocManager != null) {
							location = mlocManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								player.lat = location.getLatitude();
								player.lng = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	Runnable dynamicPoll = new Runnable() {

		@Override
		public void run() {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (player.isHasString()) {
				pairs.add(new BasicNameValuePair(Constants.potatoId, player
						.getPotatoList().get(0)));
				pairs.add(new BasicNameValuePair(Constants.temp, gameObj
						.getPotato().get(0).getTemp()
						+ ""));
			} else {
				pairs.add(new BasicNameValuePair(Constants.potatoId, ""));
				pairs.add(new BasicNameValuePair(Constants.temp, 0 + ""));
			}
			pairs.add(new BasicNameValuePair(Constants.holder, username));
			pairs.add(new BasicNameValuePair(Constants.score, player.getScore()
					+ ""));

			pairs.add(new BasicNameValuePair(Constants.lat, player.getLat()
					+ ""));
			pairs.add(new BasicNameValuePair(Constants.lng, player.getLng()
					+ ""));

			UserLoginTask task = new UserLoginTask(true, username, password,
					pairs, true, false);
			task.execute(Constants.updatePlayerInfo);
			long temp = (long) gameObj.getPotato().get(0).getTemp();
			long delay = gameObj.getMaxRoundTime() / temp / 10;
			mHandler.postDelayed(this, delay);
		}

	};
}
