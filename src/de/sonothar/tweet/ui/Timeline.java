package de.sonothar.tweet.ui;

import java.text.DateFormat;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.sonothar.tweet.R;
import de.sonothar.tweet.TimelineLoadingTask;
import de.sonothar.tweet.TweetStatus;
import de.sonothar.tweet.provider.TweetMeta;

public class Timeline extends ListFragment implements LoaderCallbacks<Cursor> {

	private TimelineAdapter mAdapter;
	private long lastTweetId = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.timeline, null);
		View timeline_loading = inflater.inflate(R.layout.timeline_loading,
				null);

		ListView list = (ListView) layout.findViewById(android.R.id.list);
		list.addFooterView(timeline_loading);

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

		Cursor c = (Cursor) getListView().getItemAtPosition(position);
		TweetStatus tweet = TweetMeta.getStatus(c);

		// TODO open fragment view with tweet and details
		((TimelineFrame) getActivity()).openStatus(tweet);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reload:
			Notification.Builder nb = new Notification.Builder(getActivity());
			Intent intent = new Intent(getActivity(), TimelineFrame.class);
			nb.setSmallIcon(R.drawable.app_icon)
					.setContentText(
							"Tweet are being pulled! Please be patient.")
					.setContentTitle("SonoTweet")
					.setContentIntent(
							PendingIntent.getActivity(getActivity(), 0, intent,
									0)).setWhen(System.currentTimeMillis())
					.setOngoing(true);
			NotificationManager nm = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(113, nb.getNotification());

			new TimelineLoadingTask(getActivity(), lastTweetId).execute();
			return true;
		case android.R.id.home:
			Toast.makeText(getActivity(), "Home: TBD!", Toast.LENGTH_LONG)
					.show();
			return true;
		case R.id.menu_clear_tweets:
			getActivity().getContentResolver().delete(TweetMeta.CONTENT_URI,
					null, null);
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

		mAdapter.changeCursor(data);
		Toast.makeText(getActivity(), "Tweets geladen", Toast.LENGTH_LONG)
				.show();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	private static class TimelineAdapter extends CursorAdapter {

		private final Context context;

		public TimelineAdapter(Context context, Cursor c) {
			super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			this.context = context;
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

		private void setTweetData(View status, Cursor cursor) {
			TweetStatus tweetStatus = TweetMeta.getStatus(cursor);

			TextView tweet = (TextView) status
					.findViewById(R.id.txt_timeline_row_tweet);
			tweet.setText(tweetStatus.getText());

			TextView user = (TextView) status
					.findViewById(R.id.txt_timeline_row_user);
			user.setText(tweetStatus.getUsername());

			TextView date = (TextView) status
					.findViewById(R.id.txt_timeline_row_date);
			DateFormat dateFormat = android.text.format.DateFormat
					.getTimeFormat(context);
			date.setText(dateFormat.format(tweetStatus.getCreatedAt()));
		}
	}
}
