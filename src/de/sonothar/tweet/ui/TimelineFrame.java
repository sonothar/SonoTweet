package de.sonothar.tweet.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.sonothar.tweet.R;
import de.sonothar.tweet.TweetStatus;

public class TimelineFrame extends Activity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.timeline_frame);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_tweet:
			startActivity(new Intent(this, CreateTweet.class));
			return true;
			// case R.id.logout:
			// clearUserdata();
			// return true;
		default:
			return false;
		}
	}

	public void openStatus(TweetStatus tweet) {
		Status status = Status.newInstance(tweet);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		transaction.add(R.id.timeline_fragment, status);
		transaction.addToBackStack(null);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN
				| FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		transaction.commit();
	}

	// private void clearUserdata() {
	// SharedPreferences preferences = getSharedPreferences(
	// Constants.USER_DATA, Context.MODE_PRIVATE);
	//
	// preferences.edit().clear().commit();
	// startActivity(new Intent(this, Overview.class));
	//
	// getContentResolver().delete(TweetMeta.CONTENT_URI, null, null);
	//
	// finish();
	// }
}
