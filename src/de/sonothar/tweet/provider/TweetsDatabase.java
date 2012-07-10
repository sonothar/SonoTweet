package de.sonothar.tweet.provider;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import de.sonothar.tweet.TweetStatus;

public class TweetsDatabase {

	public static class TweetStatusLoader extends AsyncTaskLoader<Cursor> {

		public TweetStatusLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadInBackground() {
			return TweetsDatabase.query(getContext(), null, null);
		}

		@Override
		protected void onStopLoading() {
			forceLoad();
		}
	}

	
	public static List<TweetStatus> getAllTweets(Context context){

		Cursor c = query(context, null, null);
		
		ArrayList<TweetStatus> tweets = new ArrayList<TweetStatus>();
		
		if(c.moveToFirst()){
			do{
				tweets.add(getStatus(c));
			}while(c.moveToNext());
		}
		
		return tweets;
	}
	
	public static void insertTweets(List<Status> tweets){
		
		for(Status tweet: tweets){
			TweetsDatabase.getValues(tweet);
		}
	}
	
	private static Cursor query(Context context, String where, String[] whereValues){
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TweetMeta.TABLE_NAME);

		SQLiteOpenHelper mOpenHelper = new TweetDatabaseHepler(context);

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		return qb.query(db, // The database to query
				TweetMeta.ALL_COLUMNS, // The columns to return from the query
				where, // The columns for the where clause
				whereValues, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				TweetMeta.DEFAULT_SORT_ORDER // The sort order
				);

	}
	
	private static TweetStatus getStatus(Cursor cursor) {
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
	
	private static ContentValues getValues(twitter4j.Status status) {
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
