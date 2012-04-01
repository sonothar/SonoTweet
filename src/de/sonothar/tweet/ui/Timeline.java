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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.sonothar.tweet.Constants;
import de.sonothar.tweet.R;
import de.sonothar.tweet.provider.TweetMeta;

public class Timeline extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter mAdapter;

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

		mAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null, new String[] {
						TweetMeta.TEXT, TweetMeta.SOURCE }, new int[] {
						android.R.id.text1, android.R.id.text2 }, 0);
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(0, null, this);

		new TimelineLoadingTask(getActivity()).execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		// setListAdapter(new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1, tweets));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reload:
			Toast.makeText(getActivity(), "Reload: TBD!", Toast.LENGTH_LONG)
					.show();
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
		// if (result == null) {
		// ((TextView) getListView().getEmptyView())
		// .setText("Keine Tweets gefunden!");
		// Toast.makeText(getSherlockActivity(), "Keine Tweets gefunden.",
		// Toast.LENGTH_LONG).show();
		// return;
		// }
		// // setEmptyText(result.size() + " Tweets gefunden!");
		// Toast.makeText(getSherlockActivity(),
		// result.size() + " Tweets gefunden.", Toast.LENGTH_LONG).show();
		//
		// setListAdapter(new TimelineAdapter(getActivity(), R.id.txt_status,
		// result));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
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
