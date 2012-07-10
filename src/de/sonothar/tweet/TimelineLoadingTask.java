package de.sonothar.tweet;

import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_KEY;
import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_SECRET;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import de.sonothar.tweet.provider.TweetMeta;

public class TimelineLoadingTask extends AsyncTask<Void, Void, Void> {

	private final Context context;

	// private final long lastTweetId;

	public TimelineLoadingTask(Context context, long lastTweetId) {
		this.context = context;
		// this.lastTweetId = lastTweetId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		SharedPreferences settings = context.getSharedPreferences(
				Constants.USER_DATA, Context.MODE_PRIVATE);

		String accessToken = settings.getString("twitter_access_token", null);
		String accessTokenSecret = settings.getString(
				"twitter_access_token_secret", null);

		Configuration conf = new ConfigurationBuilder()
				.setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
				.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret).build();
		Twitter t = new TwitterFactory(conf).getInstance();
		ResponseList<twitter4j.Status> timeline = null;

		try {
			Paging p = new Paging(settings.getLong("since_id", 1));
			timeline = t.getHomeTimeline(p);
			// timeline = t.getHomeTimeline(new Paging(lastTweetId));
		} catch (TwitterException e) {
			Log.e(TimelineLoadingTask.class.getSimpleName(),
					"Error while loading timeline.", e);
			return null;
		}

		Log.i(TimelineLoadingTask.class.getSimpleName(), //
				timeline.size() + " entries found.");

		ContentValues[] values = new ContentValues[timeline.size()];

		if (timeline.size() > 0) {
			settings.edit() //
					.putLong("since_id", timeline.get(0).getId()) //
					.commit();
		}

		for (int i = 0; i < timeline.size(); i++) {
			values[i] = TweetMeta.getValues(timeline.get(i));
		}

		// for now all previous data will be deleted. needs better handling
		// later.
		// context.getContentResolver().delete(TweetMeta.CONTENT_URI, null,
		// null);
		int insertCount = context.getContentResolver().bulkInsert(
				TweetMeta.CONTENT_URI, values);
		Log.i(TimelineLoadingTask.class.getSimpleName(), "Es wurden "
				+ insertCount + " EintrŠge gespeichert.");

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Notification.Builder nb = new Notification.Builder(context);
		nb.setContentText("Tweet are here. Have fun ;-)")
				.setSmallIcon(R.drawable.app_icon).setContentTitle("SonoTweet")
				.setOngoing(false);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(113, nb.getNotification());
	}

}
