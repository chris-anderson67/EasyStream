package cs.tufts.edu.easy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cs.tufts.edu.easy.R;

public class Bathroom_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom__details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bathroom Details");
        Intent mIntent = getIntent();
        int tag = mIntent.getIntExtra("bathroom_id", 0);
        String s = "";
        JSONArray jArray = null;
        //boolean value determines whether you're requesting all bathrooms data
        try {
           s = new GetData(false).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            jArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                if (oneObject.getInt("bathroom_id") == tag){
                    TextView id_text = (TextView) findViewById(R.id.bathroom_id);
                    id_text.append(oneObject.getString("comments"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



}
