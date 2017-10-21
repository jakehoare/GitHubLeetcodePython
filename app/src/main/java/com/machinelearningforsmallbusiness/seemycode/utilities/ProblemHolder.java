package com.machinelearningforsmallbusiness.seemycode.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.machinelearningforsmallbusiness.seemycode.DisplayCodeActivity;
import com.machinelearningforsmallbusiness.seemycode.R;

import java.util.HashMap;



class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView mDifficultyIcon;
    private final TextView mProblemName;

    private HashMap<String, String> problem;
    private Context context;

    ProblemHolder(Context context, View itemView) {

        super(itemView);

        this.context = context;

        this.mDifficultyIcon = (ImageView) itemView.findViewById(R.id.iv_difficulty_icon);
        this.mProblemName = (TextView) itemView.findViewById(R.id.tv_name);

        itemView.setOnClickListener(this);
    }

    void bindProblem(HashMap<String, String> problem) {

        this.problem = problem;
        this.mProblemName.setText(problem.get("name"));
        this.mDifficultyIcon.setImageResource(Integer.parseInt(problem.get("icon")));
    }

    @Override
    public void onClick(View v) {

        if (problem != null) {
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
}