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
    public String name;
    public String screenName;

    // empty constructor needed for parceler
    public Profile() {}

    // Deserialize JSON
    public static Profile fromJSON(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();

        // Extract values from JSON
        try {

            profile.backgroundUrl = jsonObject.getString("profile_banner_url");

        } catch (Exception e) {
            e.printStackTrace();
        }

        profile.profileImageUrl = jsonObject.getString("profile_image_url");
        profile.tagline = jsonObject.getString("description");
        profile.followingCount = jsonObject.getLong("friends_count");
        profile.followerCount = jsonObject.getLong("followers_count");
        profile.name = jsonObject.getString("name");
        profile.screenName = jsonObject.getString("screen_name");


        return profile;
    }
}

