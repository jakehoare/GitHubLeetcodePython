package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class GetProblemsFragment extends Fragment {

    final static String GITHUB_BASE_URL =
            "https://api.github.com/repos/jakehoare/leetcode/contents";
    private String TAG = GetProblemsFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> allProblemsList;
    private ArrayList<HashMap<String, String>> filteredProblemList;

    /**
     * Callback interface through which the fragment will report the
     * results back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(ArrayList<HashMap<String, String>> problemsList);
    }

    private TaskCallbacks mCallbacks;
    private GetProblems mTask;

    @Override
    public void onAttach(Context context) {
        Log.d(getClass().getName(), "[onAttach]");
        mCallbacks = (TaskCallbacks) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "[onCreate]");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mTask = new GetProblems();
        mTask.execute();
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getName(), "[onDestroy]");
        super.onDestroy();

        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        Log.d(getClass().getName(), "[onDetach]");
        mCallbacks = null;
        super.onDetach();
    }

    /*****************************/
    /**** GETTERS AND SETTERS ****/
    /*****************************/
    public ArrayList<HashMap<String, String>> getAllProblems() {
        return allProblemsList;
    }

    public ArrayList<HashMap<String, String>> getFilteredProblems() {
        return filteredProblemList;
    }

    public void setFilteredProblems(ArrayList<HashMap<String, String>> problems) {
        filteredProblemList = problems;
    }

    /*****************************/
    /********* ASYNCTASK *********/
    /*****************************/
    private class GetProblems extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

            allProblemsList = new ArrayList<>();
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

                    // Looping through all problems
                    for (int i = 0; i < problems.length(); i++) {
                        JSONObject p = problems.getJSONObject(i);
                        String name = p.getString("name");
                        String type = p.getString("type");
                        String download_url = p.getString("download_url");

                        if (!type.equals("file") || !name.endsWith(".py"))
                            continue;
                        name = name.substring(0, name.length() - 3);
                        name = name.replaceFirst("_", ": ").replaceAll("_", " ");

                        // Temp hash map for single problem
                        HashMap<String, String> problem = new HashMap<>();

                        // Adding each child node to HashMap key => value
                        problem.put("name", name);
                        problem.put("type", type);
                        problem.put("download_url", download_url);

                        // Adding problem to problem list
                        allProblemsList.add(problem);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            filteredProblemList = allProblemsList;
            return allProblemsList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> problemsList) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(problemsList);
            }
        }
    }


}
