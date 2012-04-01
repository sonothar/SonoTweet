package de.sonothar.tweet.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class TweetDatabaseHepler extends SQLiteOpenHelper {

	/**
	 * The database that the provider uses as its underlying data store
	 */
	private static final String DATABASE_NAME = "sonotweets.db";

	/**
	 * The database version
	 */
	private static final int DATABASE_VERSION = 1;

	public TweetDatabaseHepler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TweetMeta.TABLE_NAME + " (" //
				+ TweetMeta._ID + " INTEGER PRIMARY KEY," //
				+ TweetMeta.TWEET_ID + " INTEGER NOT NULL," //
				+ TweetMeta.TEXT + " TEXT NOT NULL," //
				+ TweetMeta.CREATED_AT + " TEXT NOT NULL," //
				+ TweetMeta.RETWEET + " INTEGER NOT NULL," //
				+ TweetMeta.USER + " TEXT NOT NULL," //
				+ TweetMeta.SOURCE + " TEXT NOT NULL," //
				+ TweetMeta.RETWEET_BY_ME + " INTEGER NOT NULL" //
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TweetDatabaseHepler.class.getSimpleName(),
				"Database updated to version " + DATABASE_VERSION
						+ ". Old Data deleted.");

		db.execSQL("DROP TABLE IF EXISTS " + TweetMeta.TABLE_NAME);

		onCreate(db);
	}

}
