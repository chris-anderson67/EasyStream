package cs.tufts.edu.easy;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostData extends AsyncTask<String, Integer, HttpResponse> {
    @Override
    // Taken from http://stackoverflow.com/questions/2938502/sending-post-data-in-android
    protected HttpResponse doInBackground(String... args) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://45.55.189.20/test_post.php");

        try {
            // Set the data
          //  (name, username,comment,gender,cleanliness,rating)
            String name = (String)args[0];
            String username = args[1];
            String comments = args[2];
//            String gender = args[3];
            String cleanliness = args[3];
            String baby_station = args[4];
            String rating = args[5];
            String locked = args[6];
            String customers_only = args[7];
            String latitude = args[8];
            String longitude = args[9];
            String address = args[10];
            String active = "1";

            Log.v("***name is entered", name);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("bathroom_name", name));
            nameValuePairs.add(new BasicNameValuePair("comments", comments));
            nameValuePairs.add(new BasicNameValuePair("cleanliness", cleanliness));
            nameValuePairs.add(new BasicNameValuePair("rating_avg", rating));
            nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
            nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
            nameValuePairs.add(new BasicNameValuePair("address", address));
            nameValuePairs.add(new BasicNameValuePair("added_by", username));
            nameValuePairs.add(new BasicNameValuePair("locked", locked));
            nameValuePairs.add(new BasicNameValuePair("customers_only", customers_only));
            nameValuePairs.add(new BasicNameValuePair("baby_station", baby_station));
            nameValuePairs.add(new BasicNameValuePair("updated_by", username));
            nameValuePairs.add(new BasicNameValuePair("active", active));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Log.v("**ResponseText**", responseString);
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