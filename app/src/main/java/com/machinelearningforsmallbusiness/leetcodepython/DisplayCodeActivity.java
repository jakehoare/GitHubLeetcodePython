package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
    private String problemName;
    private String solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaycode);

        mDisplayText = findViewById(R.id.hjsv_code);
        TextView mDisplayTitle = findViewById(R.id.tv_title);
        ImageView mProblemDifficulty = findViewById(R.id.iv_problem_icon);

        Intent intentThatStartedThisActivity = getIntent();

        Bundle extras = intentThatStartedThisActivity.getExtras();
        problemName = extras.getString("EXTRA_TITLE");
        String downloadUrl = extras.getString("EXTRA_URL");
        String iconString = extras.getString("EXTRA_ICON");

        mDisplayTitle.setText(problemName);
        mProblemDifficulty.setImageResource(Integer.parseInt(iconString));

        if (savedInstanceState == null) {
            URL solutionURL = NetworkUtils.buildUrl(downloadUrl);
            new GetSolution().execute(solutionURL);
            Toast.makeText(this, R.string.toast_downloading_solution,
                    Toast.LENGTH_SHORT).show();
        } else {
            solution = savedInstanceState.getString("solution_text");
            mDisplayText.setTheme(Theme.GITHUB);
            mDisplayText.setHighlightLanguage(Language.PYTHON);
            mDisplayText.setSource(solution);
        }
    }

    // Handle menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_feedback) {
            sendFeedback();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Start a new activity for sending a feedback email
    private void sendFeedback() {
        Uri uri = Uri.parse(getString(R.string.mail_feedback_email));
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT,
                problemName);
        startActivity(mailIntent);
    }

    // Add the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.problem, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current solution
        savedInstanceState.putString("solution_text", solution);

        // Call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    private class GetSolution extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(URL... params) {
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
            solution = solution.substring(pos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // https://github.com/PDDStudio/highlightjs-android
            mDisplayText.setTheme(Theme.GITHUB);
            mDisplayText.setHighlightLanguage(Language.PYTHON);
            mDisplayText.setSource(solution);
        }
    }

}
