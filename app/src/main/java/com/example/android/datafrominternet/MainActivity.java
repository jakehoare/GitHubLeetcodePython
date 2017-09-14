/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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

public class MainActivity extends AppCompatActivity {

    final static String GITHUB_BASE_URL =
            "https://api.github.com/repos/jakehoare/leetcode/contents";

    private String TAG = MainActivity.class.getSimpleName();
    private EditText mSearchBoxEditText;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ListView mProblemListView;
    ArrayList<HashMap<String, String>> problemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mProblemListView = (ListView) findViewById(R.id.lv_problem_list);

        problemList = new ArrayList<>();
        new GetProblems().execute();
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository contents, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();   // TODO currently ignored
        // TODO https://stackoverflow.com/questions/13196234/simple-parse-json-from-url-on-android-and-display-in-listview
        URL githubSearchUrl = NetworkUtils.buildUrl(GITHUB_BASE_URL);
        //mUrlDisplayTextView.setText(githubSearchUrl.toString());
        new GithubQueryTask().execute(githubSearchUrl);
    }

    private void filterProblemList() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        // TODO add code here that updates mProblemListView to only show problems containing githubQuery
    }

    private void showJsonDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class GithubQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                showJsonDataView();
                //mSearchResultsTextView.setText(githubSearchResults);
            } else {
                showErrorMessage();
            }
        }
    }


    private class GetProblems extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,
                    "Json Data is downloading",
                    Toast.LENGTH_LONG).show();
            // TODO amend to mLoadingIndicator.setVisibility(View.VISIBLE); ???
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            URL allProblemsListURL = NetworkUtils.buildUrl(GITHUB_BASE_URL);
            String jsonStr = null;
            try {
                jsonStr = NetworkUtils.getResponseFromHttpUrl(allProblemsListURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting top-level JSON Array node
                    // https://stackoverflow.com/questions/10164741/get-jsonarray-without-array-name
                    JSONArray problems = new JSONArray(jsonStr);

                    // looping through all problems
                    for (int i = 0; i < problems.length(); i++) {
                        JSONObject p = problems.getJSONObject(i);
                        String name = p.getString("name");
                        String type = p.getString("type");

                        // _links node is JSON Object
                        JSONObject links = p.getJSONObject("_links");
                        String html = links.getString("html");

                        // tmp hash map for single problem
                        HashMap<String, String> problem = new HashMap<>();

                        // adding each child node to HashMap key => value
                        problem.put("name", name);
                        problem.put("type", type);
                        problem.put("html", html);

                        // adding problem to problem list
                        problemList.add(problem);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, problemList,
                    R.layout.list_item, new String[]{ "name","type"},
                    new int[]{R.id.tv_name, R.id.tv_type});
            mProblemListView.setAdapter(adapter);
            // TODO amend to mLoadingIndicator.setVisibility to invisible ???

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
