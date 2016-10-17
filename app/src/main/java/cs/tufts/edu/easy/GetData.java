package cs.tufts.edu.easy;

import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;




import java.net.URL;




/**
 * Created by melaniebelkin on 9/24/16.
 */

public class GetData extends AsyncTask<Void, Integer, String> {


    public static final String data_url = "http://45.55.189.20/get_people.php";
    public static final String detail_url = "http://45.55.189.20/get_details.php";
    private String url = "";

    public GetData (boolean details){
        super();
        if (details == false) {
            url = detail_url;

        } else {
            url = data_url;
        }
    }

    @Override
    protected String doInBackground(Void...params) {
        try {
            URL api = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) api.openConnection();

            InputStream is = conn.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader((is)));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            r.close();
            is.close();
            Log.e("string from stream: ", total.toString());
            return total.toString();
        } catch (MalformedURLException e) {

            Log.e("**** doInBackground(): ", e.toString());
            return null;

        } catch (IOException e) {

            Log.e("**** doInBackground(): ", e.toString());
            return null;

        }
    }


}












