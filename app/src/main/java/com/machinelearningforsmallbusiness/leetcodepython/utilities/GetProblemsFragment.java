package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.machinelearningforsmallbusiness.leetcodepython.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class GetProblemsFragment extends Fragment {

    final static String GITHUB_INDEX_URL = "https://raw.githubusercontent.com/jakehoare/leetcode/master/index.csv";
    private String TAG = GetProblemsFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> allProblemsList = new ArrayList<>();
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

        // Create allProblemsList
        private void mapProblems(BufferedReader br) {
            int[] difficultyIcons = new int[]{
                    R.drawable.easy_icon,
                    R.drawable.medium_icon,
                    R.drawable.hard_icon
            };

            String inputLine;
            try {
                while ((inputLine = br.readLine()) != null) {
                    String[] indexString = inputLine.split(",");

                    // Temp hash map for single problem
                    HashMap<String, String> problem = new HashMap<>();

                    // Adding data to HashMap key => value
                    problem.put("question_nb", indexString[0]);
                    problem.put("difficulty", indexString[1]);
                    problem.put("name", indexString[2]);
                    problem.put("download_url", indexString[3]);
                    problem.put("icon",
                            Integer.toString(difficultyIcons[Integer.parseInt(indexString[1]) - 1]));

                    // Adding problem to problem list
                    allProblemsList.add(problem);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

            HttpURLConnection conn = null;
            BufferedReader br;

            // Try to get BufferedReader from GitHub for index.csv file
            try {
                URL url = new URL(GITHUB_INDEX_URL);
                conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(in));
                    mapProblems(br);
                }

                // If offline, get BufferedReader from assets instead
            } catch (Exception e) {
                try {
                    InputStream is = getContext().getAssets().open("index.csv");
                    InputStreamReader isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    mapProblems(br);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } finally {
                    if(conn != null)
                        conn.disconnect();
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
