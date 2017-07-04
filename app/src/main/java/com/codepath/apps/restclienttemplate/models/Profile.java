package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by veviego on 7/3/17.
 */

@Parcel
public class Profile {

    // List out attributes
    public String backgroundUrl;
    public String profileImageUrl;
    public String tagline;
    public long followingCount;
    public long followerCount;

    // empty constructor needed for pareceler
    public Profile() {}

    // Deserialize JSON
    public static Profile fromJSON(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();

        // Extract values from JSON
        profile.backgroundUrl = jsonObject.getString("profile_background_image_url");
        profile.profileImageUrl = jsonObject.getString("profile_image_url");
        profile.tagline = jsonObject.getString("description");
        profile.followingCount = jsonObject.getLong("friends_count");
        profile.followerCount = jsonObject.getLong("followers_count");

        return profile;
    }
}

