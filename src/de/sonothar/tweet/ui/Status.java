package de.sonothar.tweet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.sonothar.tweet.R;
import de.sonothar.tweet.TweetStatus;

public class Status extends Fragment {

	private static final String TWEET_STATUS = "tweet_status_to_be_shown";

	private Status() {
	}

	public static Status newInstance(TweetStatus tweet) {
		Bundle b = new Bundle();
		b.putSerializable(TWEET_STATUS, tweet);

		Status status = new Status();
		status.setArguments(b);

		return status;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.status, container, false);

		Bundle b = getArguments();
		if (b == null) {
			return v;
		}

		TweetStatus tweet = (TweetStatus) b.get(TWEET_STATUS);

		// TODO set Data
		TextView status = (TextView) v.findViewById(R.id.txt_status_text);
		status.setText(tweet.getText());

		TextView user = (TextView) v.findViewById(R.id.txt_status_user_real);
		user.setText(tweet.getUsername());

		TextView userNick = (TextView) v
				.findViewById(R.id.txt_status_user_nick);
		userNick.setText(tweet.getUserNick());

		return v;
	}
}
