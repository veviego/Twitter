package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base URL
	public static final String REST_CONSUMER_KEY = "PfJsg28IXGKjDp14RNVi55OTY";
	public static final String REST_CONSUMER_SECRET = "YrsixQmsLhtPA6xek6g0zz0iPqAnP4bBuX0LmOUZxWNF6Ei34t";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}


	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	public void sendTweet(String message, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", message);
		Log.d("Send Tweet", message);
		try {
			client.post(apiUrl, params, handler);
		} catch (Exception e) {
			Log.d("Send Tweet", e.toString());
		}
	}

	// use this for extra params (replying to a tweet)
	public void sendTweet(String message, String paramKey, long paramMessage, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", message);
		params.put(paramKey, paramMessage);
		Log.d("Send Tweet", message);
		try {
			client.post(apiUrl, params, handler);
			Log.d("Params", params.toString());
		} catch (Exception e) {
			Log.d("Reply to Tweet", e.toString());
		}
	}

	// Retweet function
	public void reTweet(long id, boolean retweeted, AsyncHttpResponseHandler handler) {
		String apiUrl;

		if (retweeted) {
			apiUrl = getApiUrl("statuses/unretweet/" + id + ".json");
		} else {
			apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
		}

		try {
			client.post(apiUrl, null, handler);
		} catch (Exception e) {
			Log.d("ReTweet", e.toString());
		}
	}

	// Favorite function
	public void favorite(long id, boolean favorited, AsyncHttpResponseHandler handler) {
		String apiUrl;

		if (favorited) {
			apiUrl = getApiUrl("favorites/destroy.json");
		} else {
			apiUrl = getApiUrl("favorites/create.json");
		}

		RequestParams params = new RequestParams();
		params.put("id", id);

		try {
			client.post(apiUrl, params, handler);
		} catch (Exception e) {
			Log.d("Favorite", e.toString());
		}
	}

	// Get user timeline
	public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		client.get(apiUrl, params, handler);
	}

	// Get my profile information
	public void getMyProfile(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}

	// Get other profile information
	public void getProfile(String userName, String userID, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userID);
		params.put("screen_name", userName);
		client.get(apiUrl, params, handler);
	}






	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
