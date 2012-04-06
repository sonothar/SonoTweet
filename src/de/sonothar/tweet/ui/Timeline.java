package de.sonothar.tweet.ui;

import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_KEY;
import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_SECRET;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.sonothar.tweet.Constants;
import de.sonothar.tweet.R;
import de.sonothar.tweet.TweetStatus;
import de.sonothar.tweet.provider.TweetMeta;

public class Timeline extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	private TimelineAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.timeline, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		mAdapter = new TimelineAdapter(getActivity(), null);
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO open fragment view with tweet and details
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reload:
			Toast.makeText(getActivity(), "Reload", Toast.LENGTH_LONG).show();
			new TimelineLoadingTask(getActivity()).execute();
			return true;
		case android.R.id.home:
			Toast.makeText(getActivity(), "Home: TBD!", Toast.LENGTH_LONG)
					.show();
			return true;
		default:
			return false;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), TweetMeta.CONTENT_URI, null,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null) {
			data.setNotificationUri(getActivity().getContentResolver(),
					TweetMeta.CONTENT_URI);
		}
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	private static class TimelineAdapter extends CursorAdapter {

		public TimelineAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			setTweetData(view, cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			View status = ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.timeline_row, viewGroup, false);

			setTweetData(status, cursor);

			return status;
		}

		private TweetStatus getStatus(Cursor cursor) {
			int tweetCoursorId = cursor.getColumnIndex(TweetMeta.TEXT);
			int userCoursorId = cursor.getColumnIndex(TweetMeta.USER);
			int createdCoursorId = cursor.getColumnIndex(TweetMeta.CREATED_AT);
			int sourceCoursorId = cursor.getColumnIndex(TweetMeta.SOURCE);
			int retweetCoursorId = cursor.getColumnIndex(TweetMeta.RETWEET);
			int retweetByMeCoursorId = cursor
					.getColumnIndex(TweetMeta.RETWEET_BY_ME);
			int tweetIdCoursorId = cursor.getColumnIndex(TweetMeta.TWEET_ID);

			return new TweetStatus(cursor.getLong(tweetIdCoursorId),
					cursor.getString(tweetCoursorId),
					cursor.getString(userCoursorId),
					cursor.getString(sourceCoursorId),
					cursor.getLong(createdCoursorId),
					cursor.getInt(retweetCoursorId) > 0,
					cursor.getInt(retweetByMeCoursorId) > 0);
		}

		private void setTweetData(View status, Cursor cursor) {
			TweetStatus tweetStatus = getStatus(cursor);

			TextView user = (TextView) status
					.findViewById(R.id.txt_timeline_row_user);
			user.setText(tweetStatus.getUsername());

			TextView date = (TextView) status
					.findViewById(R.id.txt_timeline_row_date);
			date.setText(tweetStatus.getCreatedAt().toLocaleString());

			TextView tweet = (TextView) status
					.findViewById(R.id.txt_timeline_row_tweet);
			tweet.setText(tweetStatus.getText());

			TextView source = (TextView) status
					.findViewById(R.id.txt_timeline_row_source);
			source.setText(Html.fromHtml(tweetStatus.getSource()));
		}
	}

	private static class TimelineLoadingTask extends
			AsyncTask<Void, Void, Void> {

		private final Context context;

		private TimelineLoadingTask(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferences settings = context.getSharedPreferences(
					Constants.USER_DATA, Context.MODE_PRIVATE);

			String accessToken = settings.getString("twitter_access_token",
					null);
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
				timeline = t.getHomeTimeline();
			} catch (TwitterException e) {
				Log.e(TimelineLoadingTask.class.getSimpleName(),
						"Error while loading timeline.", e);
				return null;
			}

			Log.i(TimelineLoadingTask.class.getSimpleName(), //
					timeline.size() + " entries found.");

			ContentValues[] values = new ContentValues[timeline.size()];

			for (int i = 0; i < timeline.size(); i++) {
				values[i] = getValues(timeline.get(i));
			}

			context.getContentResolver().delete(TweetMeta.CONTENT_URI, null,
					null);
			int insertCount = context.getContentResolver().bulkInsert(
					TweetMeta.CONTENT_URI, values);
			Log.i(TimelineLoadingTask.class.getSimpleName(), "Es wurden "
					+ insertCount + " Einträge gespeichert.");

			return null;
		}

		private ContentValues getValues(twitter4j.Status status) {
			ContentValues cv = new ContentValues();

			cv.put(TweetMeta.TWEET_ID, status.getId());
			cv.put(TweetMeta.TEXT, status.getText());
			cv.put(TweetMeta.CREATED_AT, status.getCreatedAt().getTime());
			cv.put(TweetMeta.RETWEET, status.isRetweet());
			cv.put(TweetMeta.USER, status.getUser().getName());
			cv.put(TweetMeta.SOURCE, status.getSource());
			cv.put(TweetMeta.RETWEET_BY_ME, status.isRetweetedByMe());

			return cv;
		}
	}
}
