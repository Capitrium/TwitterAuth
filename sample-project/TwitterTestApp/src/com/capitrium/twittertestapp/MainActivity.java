package com.capitrium.twittertestapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capitrium.twitterauth.TwitterTasks;
import com.capitrium.twitterauth.TwitterValues;

public class MainActivity extends Activity {
	
	private static Button b;
	private static TextView tv;
	
	public View.OnClickListener buttonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!TwitterValues.TWITTER_IS_LOGGED_IN) {
				new TwitterTasks.TwitterGetRequestTokenTask(MainActivity.this).execute();				
			} else {
				TwitterTasks.logoutFromTwitter();
				tv.setText("No user is currently logged in...");
				b.setText("Login");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) findViewById(R.id.textview);
		b = (Button) findViewById(R.id.twitterButton);
		b.setOnClickListener(buttonListener);
		
		processTwitterLogin();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		changeLoginButton();
	}
	
	public void processTwitterLogin() {
		Uri uri = getIntent().getData();
		if(uri != null) Log.i("MainActivity", "returned url from twitter: " + uri.toString());
		if(uri != null && uri.toString().startsWith(TwitterValues.TWITTER_CALLBACK_URL)) {
			Log.i("MainActivity", "uri contains oauth_verifier");
			String verifier = uri.getQueryParameter(TwitterValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
			new TwitterTasks.TwitterGetAccessTokenTask(MainActivity.this, MainActivity.this).execute(verifier);
		}
	}
	
	public static void changeLoginButton() {
		if(TwitterValues.TWITTER_IS_LOGGED_IN) {
			tv.setText("Hello, " + TwitterValues.TWITTER_USER.getName() + "!");
			b.setText("Logout");
		} else {
			tv.setText("No user is currently logged in...");
			b.setText("Login");
		}
	}
}