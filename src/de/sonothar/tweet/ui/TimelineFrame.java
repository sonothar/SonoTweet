package de.sonothar.tweet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.sonothar.tweet.R;

public class TimelineFrame extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.timeline_frame);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu, menu);
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

	public void openStatus() {
		Status status = new Status();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		transaction.add(R.id.timeline_fragment, status);
		transaction.addToBackStack(null);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE
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
