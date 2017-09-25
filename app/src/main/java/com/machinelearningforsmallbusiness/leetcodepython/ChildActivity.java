package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.machinelearningforsmallbusiness.leetcodepython.utilities.NetworkUtils;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;

import java.io.IOException;
import java.net.URL;

public class ChildActivity extends AppCompatActivity {

    private String TAG = ChildActivity.class.getSimpleName();
    private HighlightJsView mDisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mDisplayText = (HighlightJsView) findViewById(R.id.tv_display);

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
            // no Toast because shown when orientation changes
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

            if (solution == null) {
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
            // TODO syntax highlighting
            // http://hilite.me/
            // string out from github html
            // https://github.com/kbiakov/CodeView-android
            // WebView
            // https://stackoverflow.com/questions/11987660/android-syntax-highlighting

            // https://github.com/PDDStudio/highlightjs-android
            mDisplayText.setHighlightLanguage(Language.AUTO_DETECT);
            mDisplayText.setSource(solution);
        }
    }

}
