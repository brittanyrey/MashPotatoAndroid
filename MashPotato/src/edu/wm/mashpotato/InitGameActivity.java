package edu.wm.mashpotato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitGameActivity extends Activity {

	private Button createGameButton;
	private Button joinButton;
	private Button logoutButton;

	private String username = "userbase";
	private String password = "password";
	private String response = "fail";
	
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
				intent.putExtra("username", username
						.toString());
				intent.putExtra("password", password
						.toString());
				finish();
				startActivity(intent);
			}
		});

		joinButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("join a game");
				Intent intent = new Intent(getApplicationContext(),
						JoinActivity.class);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
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

}
