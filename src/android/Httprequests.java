
package com.oby.cordova.plugin;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.io.IOException;
import org.json.JSONException;

import android.util.Log;
import java.util.Scanner;
import android.os.Build;

public class Httprequests {
    private static final String TAG = "Httprequests";
	private static final int CONNECTION_TIMEOUT = 30;
	private static final int DATARETRIEVAL_TIMEOUT = 60;

	public static JSONObject requestWebService(String serviceUrl) {
	    disableConnectionReuseIfNecessary();
	 
	    HttpURLConnection urlConnection = null;
	    try {
	        // create connection
	        URL urlToRequest = new URL(serviceUrl);
	        urlConnection = (HttpURLConnection) 
	            urlToRequest.openConnection();
	        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
	        urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
	         
	        // handle issues
	        int statusCode = urlConnection.getResponseCode();
	        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
	            // handle unauthorized (if service requires user login)
	        } else if (statusCode != HttpURLConnection.HTTP_OK) {
	            // handle any other errors, like 404, 500,..
	        }
	         
	        // create JSON object from content
	        InputStream in = new BufferedInputStream(
	            urlConnection.getInputStream());
	        return new JSONObject(getResponseText(in));
	         
	    } catch (MalformedURLException e) {
	        // URL is invalid
	    } catch (SocketTimeoutException e) {
	        // data retrieval or connection timed out
	    } catch (IOException e) {
	        // could not read response body 
	        // (could not create input stream)
	    } catch (JSONException e) {
	        // response body is no valid JSON string
	    } finally {
	        if (urlConnection != null) {
	            urlConnection.disconnect();
	        }
	    }       
	     
	    return null;
	}
	 
	/**
	 * required in order to prevent issues in earlier Android version.
	 */
	private static void disableConnectionReuseIfNecessary() {
	    // see HttpURLConnection API doc
	    if (Integer.parseInt(Build.VERSION.SDK) 
	            < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}
	 
	private static String getResponseText(InputStream inStream) {
	    // very nice trick from 
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}
}