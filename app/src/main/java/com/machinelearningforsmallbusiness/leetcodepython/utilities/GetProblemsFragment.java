package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.machinelearningforsmallbusiness.leetcodepython.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class GetProblemsFragment extends Fragment {

    final static String GITHUB_BASE_URL = "https://api.github.com/repos/jakehoare/leetcode/contents";
    final static String LEETCODE_API_URL = "https://leetcode.com/api/problems/algorithms/";
    private String TAG = GetProblemsFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> allProblemsList;
    private ArrayList<HashMap<String, String>> filteredProblemList;
    private HashMap<String, String> difficultyMapping;

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

    /**** GETTERS AND SETTERS ****/
    public ArrayList<HashMap<String, String>> getAllProblems() {
        return allProblemsList;
    }

    public ArrayList<HashMap<String, String>> getFilteredProblems() {
        return filteredProblemList;
    }

    public void setFilteredProblems(ArrayList<HashMap<String, String>> problems) {
        filteredProblemList = problems;
    }

    /********* ASYNCTASK *********/
    private class GetProblems extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

            // Get mapping from problem to difficulty from leetcode API
            URL leetcodeAPIURL = NetworkUtils.buildUrl(LEETCODE_API_URL);
            String jsonLeetocdeString = NetworkUtils.getJsonfromURL(leetcodeAPIURL);
            Log.d(TAG, "Response from Leetcode url: " + jsonLeetocdeString);
            parseLeetcodeJson(jsonLeetocdeString);

            // Get solutions from GitHub
            allProblemsList = new ArrayList<>();
            URL allProblemsListURL = NetworkUtils.buildUrl(GITHUB_BASE_URL);
            String jsonProblemsString = NetworkUtils.getJsonfromURL(allProblemsListURL);
            Log.d(TAG, "Response from Github url: " + jsonProblemsString);
            parseProblemsJson(jsonProblemsString);

            filteredProblemList = allProblemsList;
            return allProblemsList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> problemsList) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(problemsList);
            }
        }

        private void parseProblemsJson(String jsonProblemsString) {
            if (jsonProblemsString != null) {
                try {
                    // Getting top-level JSON Array node
                    // https://stackoverflow.com/questions/10164741/get-jsonarray-without-array-name
                    JSONArray problems = new JSONArray(jsonProblemsString);

                    // Array of integers points to images stored in /res/drawable-ldpi/
                    int[] difficultyIcons = new int[]{
                            R.drawable.easy_icon,
                            R.drawable.medium_icon,
                            R.drawable.hard_icon
                    };

                    // Looping through all problems
                    for (int i = 0; i < problems.length(); i++) {
                        JSONObject p = problems.getJSONObject(i);
                        String name = p.getString("name");
                        String type = p.getString("type");
                        String download_url = p.getString("download_url");

                        // Remove non-Python
                        if (!type.equals("file") || !name.endsWith(".py"))
                            continue;

                        // Remove suffix, replace underscores
                        name = name.substring(0, name.length() - 3);
                        name = name.replaceFirst("_", ": ").replaceAll("_", " ");
                        // Get number without leading zeros
                        String questionString = name.substring(0, name.indexOf(' ') - 1);
                        int questionInt = Integer.parseInt(questionString);
                        questionString = Integer.toString(questionInt);
                        String difficultyString = difficultyMapping.get(questionString);
                        int difficultyInt;

                        // Temp hash map for single problem
                        HashMap<String, String> problem = new HashMap<>();

                        // Adding data to HashMap key => value
                        problem.put("name", name);
                        problem.put("type", type);
                        problem.put("download_url", download_url);
                        problem.put("question_nb", questionString);

                        try {   // default to medium if no stated difficulty
                            difficultyInt = Integer.parseInt(difficultyString);
                        } catch (NumberFormatException e) {
                            difficultyInt = 2;
                            difficultyString = "2";
                        }
                        problem.put("difficulty", difficultyString);
                        problem.put("icon",
                                Integer.toString(difficultyIcons[difficultyInt - 1]));

                        // Adding problem to problem list
                        allProblemsList.add(problem);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
        }

        private void parseLeetcodeJson(String jsonLeetocdeString) {
            if (jsonLeetocdeString != null) {
                try {
                    // Getting top-level JSON Object node
                    JSONObject leetcode = new JSONObject(jsonLeetocdeString);
                    JSONArray problems = leetcode.getJSONArray("stat_status_pairs");
                    difficultyMapping = new HashMap<>();

                    // Looping through all problems
                    for (int i = 0; i < problems.length(); i++) {
                        JSONObject problem = problems.getJSONObject(i);
                        String questionID = problem.getJSONObject("stat").getString("question_id");
                        String difficultyLevel = problem.getJSONObject("difficulty").getString("level");

                        // Adding each problem HashMap question => difficulty
                        difficultyMapping.put(questionID, difficultyLevel);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
        }

    }


}
