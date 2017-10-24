package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.machinelearningforsmallbusiness.leetcodepython.DisplayCodeActivity;

import java.util.HashMap;

public class ShowSolution {

    public static void showSolution(HashMap<String, String> problem, Context context) {
        String downloadUrl = problem.get("download_url");
        String problemName = problem.get("name");
        String iconString = problem.get("icon");
        Class destinationActivity = DisplayCodeActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_URL", downloadUrl);
        extras.putString("EXTRA_TITLE", problemName);
        extras.putString("EXTRA_ICON", iconString);
        startChildActivityIntent.putExtras(extras);
        context.startActivity(startChildActivityIntent);
    }
}
