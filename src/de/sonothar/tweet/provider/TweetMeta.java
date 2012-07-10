package de.sonothar.tweet.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import de.sonothar.tweet.TweetStatus;

public final class TweetMeta implements BaseColumns {

	// Class does not need a constructor
	private TweetMeta() {
	}

	public static String AUTHORITY = "de.sonothar.tweet.provider.tweets";

	public static final String TABLE_NAME = "tweets";

	/*
	 * URI definitions
	 */

	private static final String SCHEME = "content://";

	/**
	 * Path parts for the URIs
	 */
	static final String PATH_TWEETS = "tweets";
	static final String PATH_TWEETS_ID = PATH_TWEETS + "/";
	static final String PATH_LAST_TWEET_ID = "last_tweet_id";
	static final int TWEET_ID_PATH_POSITION = 1;

	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/"
			+ PATH_TWEETS);

	/**
	 * The content URI base for a single note. Callers must append a numeric
	 * note id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY
			+ "/" + PATH_TWEETS_ID);

	/**
	 * The content URI match pattern for a single note, specified by its ID. Use
	 * this to match incoming URIs or to construct an Intent.
	 */
	public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
			+ AUTHORITY + "/" + PATH_TWEETS_ID + "#");

	/*
	 * MIME type definitions
	 */

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.sonothar.tweets";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.sonothar.tweet";

	/*
	 * Column definitions
	 */
	public static String TWEET_ID = "tweet_id";
	public static String TEXT = "text";
	public static String CREATED_AT = "created_at";
	public static String RETWEET = "retweet";
	public static String USER = "user";
	public static String USER_NICK = "user_nickname";
	public static String SOURCE = "source";
	public static String RETWEET_BY_ME = "retweet_by_me";

	public static String[] ALL_COLUMNS = { TWEET_ID, TEXT, CREATED_AT, RETWEET,
			USER, USER_NICK, SOURCE, RETWEET_BY_ME };

	// TODO Provider um mehr Userdaten oder gar eine Usertabelle erweitern.

	/**
	 * The default sort order for this table
	 */
	public static final String DEFAULT_SORT_ORDER = TWEET_ID + " DESC";

	public static TweetStatus getStatus(Cursor cursor) {
		int tweetCoursorId = cursor.getColumnIndex(TweetMeta.TEXT);
		int userCoursorId = cursor.getColumnIndex(TweetMeta.USER);
		int userNickCoursorId = cursor.getColumnIndex(TweetMeta.USER_NICK);
		int createdCoursorId = cursor.getColumnIndex(TweetMeta.CREATED_AT);
		int sourceCoursorId = cursor.getColumnIndex(TweetMeta.SOURCE);
		int retweetCoursorId = cursor.getColumnIndex(TweetMeta.RETWEET);
		int retweetByMeCoursorId = cursor
				.getColumnIndex(TweetMeta.RETWEET_BY_ME);
		int tweetIdCoursorId = cursor.getColumnIndex(TweetMeta.TWEET_ID);

		return new TweetStatus(cursor.getLong(tweetIdCoursorId),
				cursor.getString(tweetCoursorId),
				cursor.getString(userCoursorId),
				cursor.getString(userNickCoursorId),
				cursor.getString(sourceCoursorId),
				cursor.getLong(createdCoursorId),
				cursor.getInt(retweetCoursorId) > 0,
				cursor.getInt(retweetByMeCoursorId) > 0);
	}

	public static ContentValues getValues(twitter4j.Status status) {
		ContentValues cv = new ContentValues();

		cv.put(TweetMeta.TWEET_ID, status.getId());
		cv.put(TweetMeta.TEXT, status.getText());
		cv.put(TweetMeta.CREATED_AT, status.getCreatedAt().getTime());
		cv.put(TweetMeta.RETWEET, status.isRetweet());
		cv.put(TweetMeta.USER, status.getUser().getName());
		cv.put(TweetMeta.USER_NICK, status.getUser().getScreenName());
		cv.put(TweetMeta.SOURCE, status.getSource());
		cv.put(TweetMeta.RETWEET_BY_ME, status.isRetweetedByMe());

		return cv;
	}

}
