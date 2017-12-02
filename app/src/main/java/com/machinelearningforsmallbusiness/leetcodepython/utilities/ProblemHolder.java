package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.machinelearningforsmallbusiness.leetcodepython.DisplayCodeActivity;
import com.machinelearningforsmallbusiness.leetcodepython.R;

import java.util.HashMap;



class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView mDifficultyIcon;
    private final TextView mProblemName;

    private HashMap<String, String> problem;
    private Context context;

    ProblemHolder(Context context, View itemView) {

        super(itemView);

        this.context = context;

        this.mDifficultyIcon = itemView.findViewById(R.id.iv_difficulty_icon);
        this.mProblemName = itemView.findViewById(R.id.tv_name);

        itemView.setOnClickListener(this);
    }

    void bindProblem(HashMap<String, String> problem) {

        this.problem = problem;
        this.mProblemName.setText(problem.get("name"));
        this.mDifficultyIcon.setImageResource(Integer.parseInt(problem.get("icon")));
    }

    @Override
    public void onClick(View v) {

        if (problem != null)
            ShowSolution.showSolution(problem, context);
    }
}