package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.content.Intent;

import com.machinelearningforsmallbusiness.leetcodepython.DisplayHelpActivity;

public class ShowHelp {

    public static void showHelp(Context context) {
        Class destinationActivity = DisplayHelpActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        context.startActivity(startChildActivityIntent);
    }
}
