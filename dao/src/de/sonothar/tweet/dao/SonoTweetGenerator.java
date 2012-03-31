package de.sonothar.tweet.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class SonoTweetGenerator {

	public static void main(String[] args) throws Exception {
		Schema schema = new Schema(1, "de.sonothar.tweet");

		Entity tweet = schema.addEntity("Tweet");
		tweet.addIdProperty();
		tweet.addStringProperty("text");
		tweet.addDateProperty("created");
		tweet.addBooleanProperty("retweet");
		tweet.addStringProperty("user");
		tweet.addStringProperty("source");
		tweet.addBooleanProperty("retweetedByMe");

		new DaoGenerator().generateAll(schema, "../src-dao");

	}
}
