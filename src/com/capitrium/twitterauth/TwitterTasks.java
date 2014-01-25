package com.capitrium.twitterauth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class TwitterTasks {

	private static final String LOGGING_TAG = "TwitterTasks";
	
	/**
	 * AsyncTask used to get a RequestToken, starting the Twitter login process
	 * for the current user.
	 * 
	 * <p>
	 * In order to launch the Twitter login page on the device's browser, the
	 * Activity calling this method must have the following in the
	 * intent-filter section of it's manifest declaration:
	 * <pre>
	 * <code>
	 * <category android:name="android.intent.category.BROWSABLE" />
	 * </code>
	 * </pre>
	 * 
	 * You may call this method with the following:
	 * <pre>
	 * <code>
	 * new TwitterTasks.TwitterGetRequestTokenTask(YourActivity.this).execute();
	 * </code>
	 * </pre>
	 * </p>
	 */
	public static class TwitterGetRequestTokenTask extends AsyncTask<Void, Void, RequestToken> {

		private Activity caller;

		public TwitterGetRequestTokenTask(Activity caller) {
			super();
			this.caller = caller;
		}
		
		@Override
		protected void onPostExecute(RequestToken requestToken) {
			Intent intent;
			if(TwitterValues.TWITTER_HAS_AUTHORIZED_APP) {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));				
			} else {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL()));	
			}
			caller.startActivity(intent);
			caller.finish();
		}

		@Override
		protected RequestToken doInBackground(Void... params) {
			return TwitterUtil.getInstance().getRequestToken();
		}
	}
	
	/**
	 * AsyncTask used to get an AccessToken to access the user's Twitter data;
	 * launches ReturningUserActivity if we successfully got an AccessToken,
	 * otherwise UserLoginActivity is relaunched
	 * 
	 * <p>
	 * Set up an activity to start after the user logs in on Twitter's webpage
	 * by adding the following line to it's declaration in the manifest:
	 * <pre>
	 * <code>
	 * <data android:scheme="oauth" android:host="twitter.login" />
	 * </code>
	 * </pre>
	 * 
	 * Then call this method from the {@link android.app.Activity#onCreate()}
	 * method using the following:
	 * <pre>
	 * <code>
	 * Uri uri = getIntent().getData();
	 * if(uri != null) {
	 * 	String verifier = uri.getQueryParameter(TwitterValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
	 * 	new TwitterTasks.TwitterGetAccessTokenTask(YourActivity.this).execute();
	 * }
	 * </code>
	 * </pre>
	 * </p>
	 */
	public static class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {
		
		private Activity caller, successfulLoginActivity;

		public TwitterGetAccessTokenTask(Activity caller, Activity success) {
			super();
			this.caller = caller;
			this.successfulLoginActivity = success;
		}
		
		@Override
		protected void onPostExecute(String s) {
			Intent intent = null;
			if(TwitterValues.TWITTER_IS_LOGGED_IN) {
				intent = new Intent(caller, successfulLoginActivity.getClass());
				caller.startActivity(intent);
				caller.finish();
			} else {
				caller.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(caller,
							"Failed to login via Twitter; please check"
								+ " your internet connection and try again.",
							Toast.LENGTH_SHORT)
						.show();
					}
				});
			}
		}

		@Override
		protected String doInBackground(String... params) {
			
			String verifier = params[0];
			Log.i("TwitterTasks", "verifier passed was: " + verifier);

			Twitter twitter = TwitterUtil.getInstance().getTwitter();
			RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
			
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
				TwitterValues.TWITTER_OAUTH_TOKEN = accessToken.getToken();
				TwitterValues.TWITTER_OAUTH_TOKEN_SECRET = accessToken.getTokenSecret();
				TwitterValues.TWITTER_IS_LOGGED_IN = true;
				TwitterValues.TWITTER_HAS_AUTHORIZED_APP = true;
				TwitterValues.TWITTER_USER = TwitterUtil.getInstance().getTwitter()
						.showUser(accessToken.getUserId());
			} catch (TwitterException e) {
				e.printStackTrace();
			}

			return null;
		}
		
	}
	
	/**
	 * An AsyncTask which will use the access token/secret for the current user
	 * to post a status to their Twitter page.
	 *
	 * <p>
	 * Call using the following:
	 * <pre>
	 * <code>
	 * new TwitterTasks.TwitterPostStatusTask().execute("Status message");
	 * </code>
	 * </pre>
	 * </p>
	 *
	 */
	public static class TwitterPostStatusTask extends AsyncTask<String, String, String> {
		
		protected String doInBackground(String... args) {
			Log.d("Tweet Text", "> " + args[0]);
			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TwitterValues.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TwitterValues.TWITTER_CONSUMER_SECRET);

				AccessToken accessToken = new AccessToken(
					TwitterValues.TWITTER_OAUTH_TOKEN,
					TwitterValues.TWITTER_OAUTH_TOKEN_SECRET
				);

				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

				Log.i(LOGGING_TAG, "Attempting to post tweet with message: " + status);
				if(status != null && status.length() > 0) {
					twitter4j.Status response = twitter.updateStatus(status);
					Log.i(LOGGING_TAG, response.getText());
				} else {
					Log.e(LOGGING_TAG, "Unable to submit an empty message!");
				}
			} catch (TwitterException e) {
				if(e.getErrorCode() == 187)
					Log.i(LOGGING_TAG, "This message has already been posted to Twitter!");
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * Wrapper for {@link com.jnickel.androidframework.TwitterUtil.reset()};
	 * remember to use this in whatever logout method you create.
	 */
	public static void logoutFromTwitter() {
		TwitterUtil.getInstance().reset();
		TwitterValues.TWITTER_IS_LOGGED_IN = false;
	}

}
