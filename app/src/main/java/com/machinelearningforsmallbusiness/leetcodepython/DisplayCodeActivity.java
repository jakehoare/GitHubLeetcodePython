package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.ActivityNotFoundException;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        TextView mDisplayTitle = findViewById(R.id.tv_code_title);
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

        if (itemThatWasClickedId == R.id.action_feedback_problem) {
            sendFeedback();
            return true;
        }
        if (itemThatWasClickedId == R.id.action_forward) {
            forwardSolution();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Start a new activity for sending a feedback email
    private void sendFeedback() {
        Uri uri = Uri.parse(getString(R.string.mail_feedback_email));
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, problemName);
        try {
            startActivity(mailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.no_email,
                    Toast.LENGTH_LONG).show();
        }
    }

    //Start a new activity for forwarding the solution by email
    private void forwardSolution() {
        Uri uri = Uri.parse(getString(R.string.mail_blank));
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.forward_subject) + problemName);
        mailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.forward_source) + solution);
        try {
            startActivity(mailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.no_email,
                    Toast.LENGTH_LONG).show();
        }
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
                StringBuilder codeFile = new StringBuilder();
                // Build a string with the text of the file
                try {
                    String urlString = params[0].toString();
                    int index = urlString.lastIndexOf('/');
                    InputStream is = getAssets().open(urlString.substring(index + 1));
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;

                    while ((line = br.readLine()) != null) {
                        codeFile.append(line);
                        codeFile.append('\n');
                    }
                    br.close();
                }
                // Problem list loaded online contains solutions not available offline
                catch (FileNotFoundException fnfe) {
                    solution = getApplicationContext().getString(R.string.solution_offline);
                    return null;
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                solution = codeFile.toString();
            } catch (NullPointerException e) {
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

            // Add line breaks in description and solution description for lines that are more than
            // 80 characters long. New break at first space after mid point.
            StringBuilder sb = new StringBuilder(solution);
            int lineStart = 1;
            int nextBreak;
            int newBreak;
            while (sb.charAt(lineStart) == '#' || sb.charAt(lineStart) == '\n') {   // until code
                nextBreak = sb.indexOf("\n", lineStart);
                newBreak = sb.indexOf(" ", lineStart + (nextBreak - lineStart) / 2);
                if (nextBreak - lineStart > 80 && newBreak < nextBreak) {
                    sb.replace(newBreak, newBreak + 1, "\n# ");
                    lineStart = nextBreak + 3;
                } else
                    lineStart = nextBreak + 1;
            }
            solution = sb.toString();

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
