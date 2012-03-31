package de.sonothar.tweet.ui;

import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_KEY;
import static de.sonothar.tweet.Constants.OAUTH_CONSUMER_SECRET;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.sonothar.tweet.Constants;
import de.sonothar.tweet.DaoMaster;
import de.sonothar.tweet.DaoMaster.DevOpenHelper;
import de.sonothar.tweet.DaoSession;
import de.sonothar.tweet.R;
import de.sonothar.tweet.Tweet;
import de.sonothar.tweet.TweetDao;

public class Timeline extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter mAdapter;
	private TweetDao mTweetDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(),
				Constants.TweetDB, null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		mTweetDao = daoSession.getTweetDao();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.timeline, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(null);
		setHasOptionsMenu(true);

		// mAdapter = new SimpleCursorAdapter(getActivity(),
		// android.R.layout.simple_list_item_2, null, new String[] {
		// TweetMeta.TEXT, TweetMeta.SOURCE }, new int[] {
		// android.R.id.text1, android.R.id.text2 }, 0);
		getLoaderManager().initLoader(0, null, this);

		new TimelineLoadingTask(getActivity(), mTweetDao).execute();
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		// return new CursorLoader(getActivity(), TweetMeta.URI, null, null,
		// null,
		// null);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

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
	}

	private static class TimelineCursorAdapter extends CursorAdapter {

		public TimelineCursorAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private static class TimelineCursorLoader extends CursorLoader {

		public TimelineCursorLoader(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Cursor loadInBackground() {

			return super.loadInBackground();
		}
	}

	private static class TimelineLoadingTask extends
			AsyncTask<Void, Void, Void> {
		private final Context context;
		private final TweetDao tweetDao;

		private TimelineLoadingTask(Context context, TweetDao tweetDao) {
			this.context = context;
			this.tweetDao = tweetDao;
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
				e.printStackTrace();
				return null;
			}

			for (int i = 0; i < timeline.size(); i++) {
				Tweet tweet = getValues(timeline.get(i));
				// TODO save in DAO
				tweetDao.insert(tweet);
			}

			// int count =
			// context.getContentResolver().bulkInsert(TweetMeta.URI,
			// cvList);
			//
			// Log.i(TimelineLoadingTask.class.getSimpleName(), "Es wurden "
			// + count + " Einträge gespeichert.");
			return null;
		}

		private Tweet getValues(twitter4j.Status status) {
			return new Tweet(status.getId(), status.getText(),
					status.getCreatedAt(), status.isRetweet(), status.getUser()
							.getName(), status.getSource(),
					status.isRetweetedByMe());
		}

	}
}
