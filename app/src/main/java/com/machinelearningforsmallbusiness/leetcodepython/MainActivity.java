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

import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.machinelearningforsmallbusiness.leetcodepython.utilities.GetProblemsFragment;
import com.machinelearningforsmallbusiness.leetcodepython.utilities.ProblemAdapter;
import com.machinelearningforsmallbusiness.leetcodepython.utilities.ShowHelp;
import com.machinelearningforsmallbusiness.leetcodepython.utilities.ShowSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GetProblemsFragment.TaskCallbacks {

    private EditText mSearchBoxEditText;
    private RecyclerView mProblemListView;
    private ArrayList<HashMap<String, String>> allProblemsList;
    private ArrayList<HashMap<String, String>> filteredProblemList;
    final Random rand = new Random();
    private GetProblemsFragment mGetProblemsFragment;
    private static final String TAG_FRAGMENT = "get_problems_fragement";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = findViewById(R.id.et_search_box);

        mSearchBoxEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    findViewById(R.id.action_search).performClick();
                    return true;
                }
                return false;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mProblemListView = findViewById(R.id.lv_problem_list);
        mProblemListView.setLayoutManager(layoutManager);

        FragmentManager fm = getSupportFragmentManager();
        mGetProblemsFragment = (GetProblemsFragment) fm.findFragmentByTag(TAG_FRAGMENT);

        // If the fragment is null then create it, which performs the AsyncTask and
        // returns and displays allProblemsList
        if (mGetProblemsFragment == null) {
            mGetProblemsFragment = new GetProblemsFragment();
            fm.beginTransaction().add(mGetProblemsFragment, TAG_FRAGMENT).commit();
        } else {
            // Retrieve problem lists from fragment and display filteredProblemList
            // https://developer.android.com/guide/topics/resources/runtime-changes.html
            allProblemsList = mGetProblemsFragment.getAllProblems();
            filteredProblemList = mGetProblemsFragment.getFilteredProblems();
            updateProblemList(filteredProblemList);
        }
    }

    // Handle menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_search) {
            if (allProblemsList == null) {
                Toast.makeText(MainActivity.this, R.string.not_downloaded,
                        Toast.LENGTH_SHORT).show();
            } else {
                hideKeyboard();
                filterProblemList();
            }
            return true;

        } else if (itemThatWasClickedId == R.id.action_random) {

            if (filteredProblemList == null) {
                Toast.makeText(MainActivity.this, R.string.not_downloaded,
                        Toast.LENGTH_SHORT).show();
            } else if (filteredProblemList.size() == 0) {
                Toast.makeText(MainActivity.this, R.string.toast_empty,
                        Toast.LENGTH_SHORT).show();
            } else {
                int randomIndex = rand.nextInt(filteredProblemList.size());
                ShowSolution.showSolution(filteredProblemList.get(randomIndex), MainActivity.this);
            }
            return true;

        } else if (itemThatWasClickedId == R.id.action_help) {
            ShowHelp.showHelp(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Add the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**** FRAGMENT CALLBACKS *****/
    @Override
    public void onPreExecute() {
        Toast.makeText(this, R.string.toast_downloading,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostExecute(ArrayList<HashMap<String, String>> problemsList) {
        allProblemsList = problemsList;
        filteredProblemList = problemsList;
        ProblemAdapter adapter = new ProblemAdapter(this, R.layout.list_item, problemsList);
        mProblemListView.setAdapter(adapter);
    }

    /********* UTILITIES *********/
    private void filterProblemList() {
        String filterText = mSearchBoxEditText.getText().toString().toLowerCase();
        filteredProblemList = new ArrayList<>();

        List<String> difficultyLevels = new ArrayList<>(Arrays.asList("easy", "medium", "hard"));
        int level = difficultyLevels.indexOf(filterText);

        if (level == -1) {          // search the problem names
            for (HashMap<String, String> problem : allProblemsList)
                if (problem.get("name").toLowerCase().contains(filterText))
                    filteredProblemList.add(problem);
        } else {                    // get problems of specific difficulty
            for (HashMap<String, String> problem : allProblemsList)
                if (Integer.parseInt(problem.get("difficulty")) == level + 1)
                    filteredProblemList.add(problem);
        }

        // store filteredProblemList in fragment
        mGetProblemsFragment.setFilteredProblems(filteredProblemList);
        updateProblemList(filteredProblemList);
    }

    private void updateProblemList(ArrayList<HashMap<String, String>> problemList) {
        if (problemList == null)
            return;
        ProblemAdapter newAdapter = new ProblemAdapter(this, R.layout.list_item, problemList);
        mProblemListView.swapAdapter(newAdapter, false);
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
