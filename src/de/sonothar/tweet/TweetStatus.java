package de.sonothar.tweet;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

@SuppressWarnings({ "serial", "deprecation" })
public class TweetStatus implements Status {

	private final String tweet;
	private final String user;
	private final String userNick;
	private final String source;
	private final Date date;
	private final boolean isRetweet;
	private final boolean isRetweetByMe;
	private final long tweetId;

	public TweetStatus(long tweetId, String tweet, String user, String userNick, String source,
			long date, boolean isRetweet, boolean isRetweetByMe) {
		this.tweetId = tweetId;
		this.tweet = tweet;
		this.user = user;
		this.userNick = userNick;
		this.source = source;
		this.date = new Date(date);
		this.isRetweet = isRetweet;
		this.isRetweetByMe = isRetweetByMe;
	}

	public String getUsername() {
		return user;
	}

	public String getUserNick() {
		return userNick;
	}

	@Override
	public Date getCreatedAt() {
		return date;
	}

	@Override
	public long getId() {
		return tweetId;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getText() {
		return tweet;
	}

	@Override
	public boolean isRetweet() {
		return isRetweet;
	}

	@Override
	public boolean isRetweetedByMe() {
		return isRetweetByMe;
	}

	/*
	 * Not supported operations
	 */
	@Override
	public int compareTo(Status another) {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public int getAccessLevel() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public URLEntity[] getURLEntities() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public Annotations getAnnotations() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public long[] getContributors() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public GeoLocation getGeoLocation() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public String getInReplyToScreenName() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public long getInReplyToStatusId() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public long getInReplyToUserId() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public Place getPlace() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public long getRetweetCount() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public Status getRetweetedStatus() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public User getUser() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public boolean isFavorited() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

	@Override
	public boolean isTruncated() {
		throw new UnsupportedOperationException(
				"Not supportet by this implementation.");
	}

}
