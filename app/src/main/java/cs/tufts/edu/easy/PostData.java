package cs.tufts.edu.easy;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostData extends AsyncTask<String, Integer, HttpResponse> {
    @Override
    // Taken from http://stackoverflow.com/questions/2938502/sending-post-data-in-android
    protected HttpResponse doInBackground(String... args) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://45.55.189.20/test_post");

        try {
            // Set the data
          //  (name, username,comment,gender,cleanliness,rating)
            String name = (String)args[0];
            String username = args[1];
            Log.v("***name is entered", name);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("message[username]", args[0]));
            nameValuePairs.add(new BasicNameValuePair("message[content]", args[1]));
            nameValuePairs.add(new BasicNameValuePair("message[app_id]", "4"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            return response;
        }
        catch (ClientProtocolException e) {
            Crashlytics.logException(e);
            Log.e("**** postMessage(): ", e.toString());
            return null;
        }
        catch (IOException e) {
            Crashlytics.logException(e);
            Log.e("**** postMessage(): ", e.toString());
            return null;
        }
    }

}