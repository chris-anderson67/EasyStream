package cs.tufts.edu.easy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import org.apache.http.HttpResponse;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void onSubmitClick(View view) {
        final EditText nameText = (EditText) findViewById(R.id.name);
        String name = nameText.getText().toString();
        final EditText usernameText = (EditText) findViewById(R.id.username);
        String username = usernameText.getText().toString();
        final EditText commentText = (EditText) findViewById(R.id.comment);
        String comment = commentText.getText().toString();

        final CheckBox maleCheck = (CheckBox) findViewById(R.id.male_check);
        final CheckBox babyCheck = (CheckBox) findViewById(R.id.baby_check);

        final CheckBox femaleCheck = (CheckBox) findViewById(R.id.female_check);
        final CheckBox neutralCheck = (CheckBox) findViewById(R.id.neutral_check);
        String gender = "unknown";
        String baby = "0";
        if (maleCheck.isChecked() && (femaleCheck.isChecked())) {
            gender = "neutral";
        }
        else if (maleCheck.isChecked()) {
            gender = "male";
        }
        else if (femaleCheck.isChecked()){
            gender = "female";
        }
        else if (neutralCheck.isChecked()) {
            gender = "neutral";
        }

        if (babyCheck.isChecked()) {
            baby = "1";
        }

        RatingBar cBar = (RatingBar) findViewById(R.id.cleanlinessBar);
        float cleanlinessF = cBar.getRating();
        String cleanliness = Float.toString(cleanlinessF);

        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar);
        float ratingF = rBar.getRating();
        String rating = Float.toString(ratingF);

        //boolean value determines whether you're requesting all bathrooms data
        AsyncTask<String, Integer, HttpResponse> task = new PostData().execute(name, username, comment, gender, cleanliness, baby, rating);

    }
}
