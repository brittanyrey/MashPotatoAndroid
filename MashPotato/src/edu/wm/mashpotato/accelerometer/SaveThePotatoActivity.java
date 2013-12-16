/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.wm.mashpotato.accelerometer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import edu.wm.mashpotato.HomeScreenActivity;
import edu.wm.mashpotato.R;
import edu.wm.mashpotato.HomeScreenActivity.UserLoginTask;
import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.Game;
import edu.wm.mashpotato.web.Player;
import edu.wm.mashpotato.web.ResponseObject;
import edu.wm.mashpotato.web.WebTask;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SaveThePotatoActivity extends Activity implements LocationListener {
	private static final String TAG = "Pedometer";
	private SharedPreferences mSettings;
	// private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	private TextView level;

	private String temperature;
	boolean ended = false;
	private TextView mStepValueView;
	private int mStepValue;
	private boolean mQuitting = false; // Set when user selected Quit from menu,
										// can be used by onPause, onStop,
										// onDestroy

	private static final int MESSAGE_SENT = 1;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	LocationManager mlocManager;
	private boolean canGetLocation;
	/**
	 * True, when service is running.
	 */
	private boolean mIsRunning;
	private String username;
	private String password;
	private Game gameObj;
	private Player player;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[ACTIVITY] onCreate");
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		username = extras.getString("username");
		password = extras.getString("password");
		gameObj = (Game) extras.get("gameObj");
		player = (Player) extras.get("player");
		mStepValue = 0;

		setContentView(R.layout.save_the_potato_screen);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				mHandler.postDelayed(updateTemp, 5000);
			}
			
		}).start();
		mUtils = Utils.getInstance();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "[ACTIVITY] onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "[ACTIVITY] onResume");
		super.onResume();

		//mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		// mPedometerSettings = new PedometerSettings(mSettings);

		//mUtils.setSpeak(mSettings.getBoolean("speak", false));

		// Read from preferences if the service was running on the last onPause
		// mIsRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (!mIsRunning) {// && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}

		/* mPedometerSettings.clearServiceRunning(); */

		mStepValueView = (TextView) findViewById(R.id.temp);
		level = (TextView) findViewById(R.id.heat);

		setLevel(mStepValue);
	}

	private void setLevel(int heat) {
		if (heat > 90) {
			level.setText("Very Hot.");
			level.setTextColor(Color.RED);
		} else if (heat > 75) {
			level.setText("Hot.");
			level.setTextColor(Color.RED);
		} else if (heat > 50) {
			level.setText("Warm.");
			level.setTextColor(Color.MAGENTA);
		} else {
			level.setText("Cool.");
			level.setTextColor(Color.BLUE);
		}
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "[ACTIVITY] onPause");
		if (mIsRunning) {
			unbindStepService();
		}

		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] onStop");
		super.onStop();
	}

	protected void onDestroy() {
		Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
		System.exit(0);
	}

	protected void onRestart() {
		Log.i(TAG, "[ACTIVITY] onRestart");
		super.onDestroy();
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.v(TAG, "Service Disconnected");
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(SaveThePotatoActivity.this,
					StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(SaveThePotatoActivity.this, StepService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		unbindService(mConnection);
	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(SaveThePotatoActivity.this,
					StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		Log.v(TAG, "Update display");
		if (mService != null && mIsRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			SharedPreferences state = getSharedPreferences("state", 0);
			SharedPreferences.Editor stateEditor = state.edit();
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				stateEditor.commit();
			}
		}
	}

	private static final int MENU_SETTINGS = 8;
	private static final int MENU_QUIT = 9;

	private static final int MENU_PAUSE = 1;
	private static final int MENU_RESUME = 2;
	private static final int MENU_RESET = 3;

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, item.getItemId() + " Menu Item ID");
		switch (item.getItemId()) {
		case MENU_PAUSE:
			unbindStepService();
			stopStepService();
			return true;
		case MENU_RESUME:
			startStepService();
			bindStepService();
			return true;
		case MENU_RESET:
			resetValues(true);
			return true;
		case MENU_QUIT:
			resetValues(false);
			unbindStepService();
			stopStepService();
			mQuitting = true;
			finish();
			return true;
		}
		return false;
	}

	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
		}
	};

	private static final int STEPS_MSG = 1;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				if (!ended) {
					mStepValue = (int) msg.arg1;
					int temp = gameObj.getPotato().get(0)
							.changeTemp(mStepValue);
					setLevel(gameObj.getPotato().get(0)
							.getTemp());
					long delay = (100 - temp) * gameObj.getMaxRoundTime() / 100;
					if (delay < 15000) {
						delay = 15000;
					} else if (delay > 60000) {
						delay = 60000;
					}
					if (temp == 100) {
						mHandler.removeCallbacks(dynamicPoll);
						mHandler.postAtFrontOfQueue(dynamicPoll);
						mHandler.post(endIt);
						ended = true;
					} else if (temp > 75) {
						mHandler.removeCallbacks(dynamicPoll);
						mHandler.postDelayed(dynamicPoll, delay);

					} else if (temp > 50) {
						mHandler.removeCallbacks(dynamicPoll);
						mHandler.postDelayed(dynamicPoll, delay);
					} else if (temp > 25) {
						mHandler.removeCallbacks(dynamicPoll);
						mHandler.postDelayed(dynamicPoll, delay);
					} else {
						mHandler.removeCallbacks(dynamicPoll);
						mHandler.postDelayed(dynamicPoll, delay);
					}
					//mStepValue = temp;
					mStepValueView.setText("" + gameObj.getPotato().get(0).getTemp());
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

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
				resp = ResponseObject.createResponse(result, true ,
						username);
				System.out.println("response: " + resp.success + " "
						+ resp.game);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resp.success) {
				gameObj = resp.game;
				player = resp.me;
			}
			if (resp.success && this.pairs.size() == 1) {
				Intent intent = null;
				intent = new Intent(getApplicationContext(),
						HomeScreenActivity.class);
				intent.putExtra(Constants.response, resp);
				Toast.makeText(getApplicationContext(), "Boom!",
						Toast.LENGTH_SHORT).show();
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("gameObj", gameObj);
				intent.putExtra("player", player);
				mHandler.removeCallbacks(updateTemp);
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
				pairs.add(new BasicNameValuePair(Constants.potatoId, gameObj
						.getPotato().get(0).getpId()));
						//player.getPotatoList().get(0)));
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
			long delay = gameObj.getMaxRoundTime() / (temp + 1) / 10;
			mHandler.postDelayed(this, delay);
		}

	};

	Runnable endIt = new Runnable() {

		@Override
		public void run() {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("playerId", username));
			UserLoginTask task = new UserLoginTask(true, username, password,
					pairs, true, false);
			task.execute(Constants.removePlayer);
		}

	};
	
	Runnable updateTemp = new Runnable() {

		@Override
		public void run() {
			gameObj.getPotato().get(0).changeTemp(mStepValue);
			mStepValueView.setText("" + gameObj.getPotato().get(0).getTemp());
			mHandler.postDelayed(this, 5000);
		}

	}; 

}
