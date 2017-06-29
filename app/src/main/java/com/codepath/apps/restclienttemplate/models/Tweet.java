package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by veviego on 6/26/17.
 */

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database id for tweet
    public User user;
    public String createdAt;
    public boolean retweeted;
    public boolean favorited;

    // empty constructor needed for pareceler
    public Tweet() {}

    // deserialize the data (JSON)
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweeted =  jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        return tweet;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}
