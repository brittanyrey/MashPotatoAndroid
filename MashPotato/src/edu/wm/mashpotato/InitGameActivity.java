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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_game_screen);

		createGameButton = (Button) findViewById(R.id.createAGame);
		joinButton = (Button) findViewById(R.id.joinGame);
		logoutButton = (Button) findViewById(R.id.logout);

		createGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("create a game");
				Intent intent = new Intent(getApplicationContext(),
						CreateActivity.class);
				finish();
				startActivity(intent);
			}
		});

		joinButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("join a game");
				Intent intent = new Intent(getApplicationContext(),
						JoinActivity.class);
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
