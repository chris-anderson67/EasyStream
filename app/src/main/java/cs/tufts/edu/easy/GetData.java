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
    public GetData (){
        super();

    }

    @Override
    protected String doInBackground(Void...params) {
        try {
            URL api = new URL(data_url);
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












