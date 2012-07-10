package de.sonothar.tweet.ui;

import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_KEY;
import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_SECRET;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.sonothar.tweet.R;

public class CreateTweet extends Activity implements LoaderCallbacks<Status> {

	private String tweetText;
	private SharedPreferences settings;
	private TextView tweetView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);

		settings = getSharedPreferences("twitter_token", Context.MODE_PRIVATE);

		String accessToken = settings.getString("twitter_access_token", null);
		String accessTokenSecret = settings.getString(
				"twitter_access_token_secret", null);

		if (accessToken == null || accessTokenSecret == null) {
			finish();
			return;
		}

		tweetView = (TextView) findViewById(R.id.txt_tweet);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return false;
		}
	}

	public void onClickSend(View view) {
		tweetText = tweetView.getText().toString();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Status> onCreateLoader(int id, Bundle bundle) {
		return new TweetLoader(this, settings, tweetText);
	}

	@Override
	public void onLoadFinished(Loader<Status> laoder, Status status) {
		if (status == null) {
			Toast.makeText(this, "Error while posting tweet.",
					Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(this, "Tweet successfully send.", Toast.LENGTH_LONG)
				.show();

		tweetView.setText("");

	}

	@Override
	public void onLoaderReset(Loader<Status> arg0) {
	}

	private static class TweetLoader extends AsyncTaskLoader<Status> {

		private final SharedPreferences settings;
		private final String tweet;

		public TweetLoader(Context context, SharedPreferences settings,
				String tweetText) {
			super(context);
			this.settings = settings;
			tweet = tweetText;
		}

		@Override
		public Status loadInBackground() {

			String accessToken = settings.getString("twitter_access_token",
					null);
			String accessTokenSecret = settings.getString(
					"twitter_access_token_secret", null);
			if (accessToken == null || accessTokenSecret == null) {
				return null;
			}

			Configuration conf = new ConfigurationBuilder()
					.setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
					.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
					.setOAuthAccessToken(accessToken)
					.setOAuthAccessTokenSecret(accessTokenSecret).build();
			Twitter t = new TwitterFactory(conf).getInstance();
			Status status = null;
			try {
				status = t.updateStatus(tweet);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return status;
		}

		@Override
		protected void onStartLoading() {
			forceLoad();
		}
	}

}
