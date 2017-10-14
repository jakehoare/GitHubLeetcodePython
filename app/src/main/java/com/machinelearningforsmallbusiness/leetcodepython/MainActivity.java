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

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.machinelearningforsmallbusiness.leetcodepython.utilities.GetProblemsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        GetProblemsFragment.TaskCallbacks {

    private EditText mSearchBoxEditText;
    private ListView mProblemListView;
    private ArrayList<HashMap<String, String>> allProblemsList;
    private ArrayList<HashMap<String, String>> filteredProblemList;
    final Random rand = new Random();
    private GetProblemsFragment mGetProblemsFragment;
    private static final String TAG_FRAGMENT = "get_problems_fragement";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mProblemListView = (ListView) findViewById(R.id.lv_problem_list);

        FragmentManager fm = getSupportFragmentManager();
        mGetProblemsFragment = (GetProblemsFragment) fm.findFragmentByTag(TAG_FRAGMENT);

        // If the fragment is null then create it, which performs the AsyncTask and
        // returns and diplays allProblemsList
        if (mGetProblemsFragment == null) {
            mGetProblemsFragment = new GetProblemsFragment();
            fm.beginTransaction().add(mGetProblemsFragment, TAG_FRAGMENT).commit();
        } else {
            // Retrieve problem lists from fragment and display filteredProblemList
            // https://developer.android.com/guide/topics/resources/runtime-changes.html
            allProblemsList = mGetProblemsFragment.getAllProblems();
            filteredProblemList = mGetProblemsFragment.getFilteredProblems();
            displayProblemList(filteredProblemList);
        }
        mProblemListView.setOnItemClickListener(this);
    }

    // Handle menu item click
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

    // Handle list item clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        showSolution(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**** FRAGMENT CALLBACKS *****/
    @Override
    public void onPreExecute() {
        Toast.makeText(this, "Problem list is downloading",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostExecute(ArrayList<HashMap<String, String>> problemsList) {
        allProblemsList = problemsList;
        filteredProblemList = problemsList;
        displayProblemList(filteredProblemList);
    }

    /********* UTILITIES *********/
    private void showSolution(int filteredIndex) {
        String downloadUrl = filteredProblemList.get(filteredIndex).get("download_url");
        String problemName = filteredProblemList.get(filteredIndex).get("name");
        String iconString = filteredProblemList.get(filteredIndex).get("icon");
        Context context = MainActivity.this;
        Class destinationActivity = DisplayCodeActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_URL", downloadUrl);
        extras.putString("EXTRA_TITLE",problemName);
        extras.putString("EXTRA_ICON",iconString);
        startChildActivityIntent.putExtras(extras);
        //startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, downloadUrl);
        //startChildActivityIntent.putExtra(Intent.EXTRA_TITLE, problemName);
        startActivity(startChildActivityIntent);
    }

    private void filterProblemList() {
        String filterText = mSearchBoxEditText.getText().toString().toLowerCase();
        filteredProblemList = new ArrayList<>();

        for (HashMap<String, String> problem : allProblemsList)
            if (problem.get("name").toLowerCase().contains(filterText))
                filteredProblemList.add(problem);

        // store filteredProblemList in fragment
        mGetProblemsFragment.setFilteredProblems(filteredProblemList);
        displayProblemList(filteredProblemList);
    }

    private void displayProblemList(ArrayList<HashMap<String, String>> problemList) {
        if (problemList == null)
            return;

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, problemList,
                R.layout.list_item, new String[]{"name", "icon"},
                new int[]{R.id.tv_name, R.id.iv_difficulty_icon});
        mProblemListView.setAdapter(adapter);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null)
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
