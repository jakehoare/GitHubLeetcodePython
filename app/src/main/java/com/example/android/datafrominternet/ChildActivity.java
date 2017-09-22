package com.example.android.datafrominternet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChildActivity extends AppCompatActivity {

    private String TAG = ChildActivity.class.getSimpleName();
    private TextView mDisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mDisplayText = (TextView) findViewById(R.id.tv_display);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String downloadUrl = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            URL solutionURL = NetworkUtils.buildUrl(downloadUrl);
            new GetSolution().execute(solutionURL);
        }
    }

    private class GetSolution extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ChildActivity.this,
                    "Solution is downloading",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(URL... params) {
            String solution = null;
            try {
                solution = NetworkUtils.getResponseFromHttpUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + solution);

            if (solution != null) {
            } else {
                Log.e(TAG, "Couldn't get solution from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get solution from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return solution;
        }

        @Override
        protected void onPostExecute(String solution) {
            //super.onPostExecute(result); TODO check if null as per E0206
            mDisplayText.setText(solution);
        }
    }

}
