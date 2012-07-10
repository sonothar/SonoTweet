package de.sonothar.tweet.ui;

import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_KEY;
import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_SECRET;
import static de.sonothar.tweet.Constants.TWITTER_AUTH;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.sonothar.tweet.Constants;
import de.sonothar.tweet.R;

public class Overview extends Activity {

	private Twitter mTwitter;
	private RequestToken mRequestToken;
	private SharedPreferences settings;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		settings = getSharedPreferences(Constants.USER_DATA,
				Context.MODE_PRIVATE);

		String accessToken = settings.getString("twitter_access_token", null);
		String accessTokenSecret = settings.getString(
				"twitter_access_token_secret", null);

		mTwitter = new TwitterFactory().getInstance();
		mTwitter.setOAuthConsumer(OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);

		if (accessToken != null && accessTokenSecret != null) {
			timeline();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			String oauthVerifier = (String) data.getExtras().get(
					"oauth_verifier");
			new GetAccessToken().execute(oauthVerifier);
		} else {
			showErrorMsg();
		}
	}

	public void onClickLogin(View view) {
		startAuth();
	}

	private void startAuth() {
		Button bt_login = (Button) findViewById(R.id.main_bt_login);
		ProgressBar progress = (ProgressBar) findViewById(R.id.main_progress);
		TextView txt_error = (TextView) findViewById(R.id.main_txt_error);

		bt_login.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);
		txt_error.setVisibility(View.GONE);

		// Twitter mTwitter and RequestToken mRequestToken
		// are private members of this activity
		new GetRequestToken().execute();
	}

	private void timeline() {
		startActivity(new Intent(this, TimelineFrame.class));
		finish();
	}

	private void showErrorMsg() {
		// Toast.makeText(Overview.this, "Fehler beim authorisieren",
		// Toast.LENGTH_LONG).show();
		Button bt_login = (Button) findViewById(R.id.main_bt_login);
		ProgressBar progress = (ProgressBar) findViewById(R.id.main_progress);
		TextView txt_error = (TextView) findViewById(R.id.main_txt_error);

		bt_login.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		txt_error.setVisibility(View.VISIBLE);
	}

	private class GetRequestToken extends AsyncTask<Void, Void, RequestToken> {

		@Override
		protected RequestToken doInBackground(Void... params) {
			String callbackURL = getResources().getString(
					R.string.twitter_callback);

			try {
				return mTwitter.getOAuthRequestToken(callbackURL);
			} catch (TwitterException e) {
				Log.e("GetRequestToken", e.getStatusCode() + "", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(RequestToken result) {
			if (result == null) {
				// TODO handle erros
				showErrorMsg();
				return;
			}

			mRequestToken = result;
			Intent i = new Intent(Overview.this, TwitterWebview.class);
			i.putExtra("URL", mRequestToken.getAuthenticationURL());
			startActivityForResult(i, TWITTER_AUTH);
		}
	}

	private class GetAccessToken extends AsyncTask<String, Void, AccessToken> {

		@Override
		protected AccessToken doInBackground(String... params) {
			String oauthVerifier = params[0];
			AccessToken at = null;
			try {
				// Pair up our request with the response
				at = mTwitter.getOAuthAccessToken(mRequestToken, oauthVerifier);
			} catch (TwitterException e) {
				e.printStackTrace();
				Log.e("GetAccessToken", e.getStatusCode() + "", e);
			}
			return at;
		}

		@Override
		protected void onPostExecute(AccessToken result) {
			if (result == null) {
				showErrorMsg();
				return;
			}

			settings.edit()
					.putString("twitter_access_token", result.getToken())
					.putString("twitter_access_token_secret",
							result.getTokenSecret()).commit();
			timeline();

		}

	}
}