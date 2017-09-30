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
package com.machinelearningforsmallbusiness.leetcodepython;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.machinelearningforsmallbusiness.leetcodepython.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final static String GITHUB_BASE_URL =
            "https://api.github.com/repos/jakehoare/leetcode/contents";

    private String TAG = MainActivity.class.getSimpleName();
    private EditText mSearchBoxEditText;
    private ListView mProblemListView;
    ArrayList<HashMap<String, String>> allProblemsList;
    ArrayList<HashMap<String, String>> filteredProblemList;
    final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mProblemListView = (ListView) findViewById(R.id.lv_problem_list);

        allProblemsList = new ArrayList<>();
        new GetProblems().execute();

        mProblemListView.setOnItemClickListener(this);
    }

    private void showSolution(int filteredIndex) {
        String downloadUrl = filteredProblemList.get(filteredIndex).get("download_url");
        String problemName = filteredProblemList.get(filteredIndex).get("name");
        Context context = MainActivity.this;
        Class destinationActivity = DisplayCodeActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, downloadUrl);
        startChildActivityIntent.putExtra(Intent.EXTRA_TITLE, problemName);
        startActivity(startChildActivityIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        showSolution(position);
    }

    private void filterProblemList() {
        String filterText = mSearchBoxEditText.getText().toString().toLowerCase();
        filteredProblemList = new ArrayList<>();

        for (HashMap<String, String> problem : allProblemsList)
            if (problem.get("name").toLowerCase().contains(filterText))
                filteredProblemList.add(problem);

        displayProblemList(filteredProblemList);
    }

    private void displayProblemList(ArrayList<HashMap<String, String>> problemList) {
        ListAdapter adapter = new SimpleAdapter(MainActivity.this, problemList,
                R.layout.list_item, new String[]{"name"},
                new int[]{R.id.tv_name});
        mProblemListView.setAdapter(adapter);
    }


    private class GetProblems extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Problem list is downloading",
                    Toast.LENGTH_LONG).show();
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
                        String download_url = p.getString("download_url");

                        if (!type.equals("file") || !name.endsWith(".py"))
                            continue;
                        name = name.substring(0, name.length() - 3);
                        name = name.replaceFirst("_", ": ").replaceAll("_", " ");

                        // temp hash map for single problem
                        HashMap<String, String> problem = new HashMap<>();

                        // adding each child node to HashMap key => value
                        problem.put("name", name);
                        problem.put("type", type);
                        problem.put("download_url", download_url);

                        // adding problem to problem list
                        allProblemsList.add(problem);
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
            // Initialise filtered list with all problems
            filteredProblemList = new ArrayList<>(allProblemsList);
            displayProblemList(filteredProblemList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null)
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_search) {
            hideKeyboard();
            filterProblemList();
            return true;

        } else if (itemThatWasClickedId == R.id.action_random) {
            if (filteredProblemList.size() == 0) {
                Toast.makeText(MainActivity.this, "Problem list is empty",
                        Toast.LENGTH_LONG).show();
            } else {
                int randomIndex = rand.nextInt(filteredProblemList.size());
                showSolution(randomIndex);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
