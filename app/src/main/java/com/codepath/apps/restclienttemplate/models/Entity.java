package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by veviego on 6/26/17.
 */

@Parcel
public class Entity {

    // list the attributes
    public long eid;
    public String media_url;


    // empty constructor needed for parceler
    public Entity() {}

    // deserialize the JSON
    public static Entity fromJSON(JSONObject json) throws JSONException {
        JSONArray info = json.getJSONArray("media");
        JSONObject first = info.getJSONObject(0);
        Entity entity = new Entity();

        // extract and fill the values
        entity.eid = first.getLong("id");
        entity.media_url = first.getString("media_url");
        return entity;
    }
}