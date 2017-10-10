package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.machinelearningforsmallbusiness.leetcodepython.utilities.NetworkUtils;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

import java.io.IOException;
import java.net.URL;

public class DisplayCodeActivity extends AppCompatActivity {

    private String TAG = DisplayCodeActivity.class.getSimpleName();
    private HighlightJsView mDisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaycode);

        mDisplayText = (HighlightJsView) findViewById(R.id.hjsv_code);
        TextView mDisplayTitle = (TextView) findViewById(R.id.tv_title);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TITLE)) {
            String problemName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TITLE);
            mDisplayTitle.setText(problemName);
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
                return null;
            }

            // Remove first 3 lines of _author_, _project_ and blank
            int linesToRemove = 3;
            int pos = solution.indexOf("\n");
            while (--linesToRemove > 0)
                pos = solution.indexOf("\n", pos + 1);
            return solution.substring(pos);
        }

        @Override
        protected void onPostExecute(String solution) {
            // https://github.com/PDDStudio/highlightjs-android
            mDisplayText.setTheme(Theme.GITHUB);
            mDisplayText.setHighlightLanguage(Language.PYTHON);
            mDisplayText.setSource(solution);
        }
    }

}
