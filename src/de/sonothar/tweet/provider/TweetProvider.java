package de.sonothar.tweet.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TweetProvider extends ContentProvider {

	private static String LOG_TAG = TweetProvider.class.getSimpleName();

	private TweetDatabaseHepler mOpenHelper;

	private static final int TWEET_ID = 1;
	private static final int TWEETS = 2;
	private static final int LAST_STATUS_ID = 3;

	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		uriMatcher.addURI(TweetMeta.AUTHORITY, TweetMeta.PATH_TWEETS_ID + "#",
				TWEET_ID);
		uriMatcher.addURI(TweetMeta.AUTHORITY, TweetMeta.PATH_TWEETS, TWEETS);
		uriMatcher.addURI(TweetMeta.AUTHORITY, TweetMeta.PATH_LAST_TWEET_ID,
				LAST_STATUS_ID);
	}

	@Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't
		// opened until
		// something tries to access it, and it's only created if it doesn't
		// already exist.
		mOpenHelper = new TweetDatabaseHepler(getContext());

		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case TWEET_ID:
			return TweetMeta.CONTENT_ITEM_TYPE;
		case TWEETS:
			return TweetMeta.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.i(LOG_TAG, "Query for URI: " + uri);

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TweetMeta.TABLE_NAME);

		switch (uriMatcher.match(uri)) {
		case TWEET_ID:
			qb.appendWhere(TweetMeta.TWEET_ID
					+ "="
					+ uri.getPathSegments().get(
							TweetMeta.TWEET_ID_PATH_POSITION));

			break;

		case TWEETS:
			break;
		case LAST_STATUS_ID:

			return null;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		String orderBy;
		// If no sort order is specified, uses the default
		if (sortOrder == null) {
			orderBy = TweetMeta.DEFAULT_SORT_ORDER;
		} else {
			// otherwise, uses the incoming sort order
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		Cursor c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				orderBy // The sort order
				);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.i(LOG_TAG, "Insert for URI: " + uri);

		if (uriMatcher.match(uri) != TWEETS) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		// Performs the insert and returns the ID of the new note.
		long rowId = db.insert(TweetMeta.TABLE_NAME, null, values);

		if (rowId > 0) {
			// Creates a URI with the note ID pattern and the new row ID
			// appended to it.
			Uri noteUri = ContentUris.withAppendedId(
					TweetMeta.CONTENT_ID_URI_BASE, rowId);
			Log.i(LOG_TAG, "URI for insert Data: " + noteUri);

			// Notifies observers registered against this provider that the data
			// changed.
			getContext().getContentResolver().notifyChange(uri, null);
			return noteUri;
		}

		Log.i(LOG_TAG, "Error while executing insert.");
		// If the insert didn't succeed, then the rowID is <= 0. Throws an
		// exception.
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		if (uriMatcher.match(uri) != TWEETS) {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		db.delete(TweetMeta.TABLE_NAME, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
