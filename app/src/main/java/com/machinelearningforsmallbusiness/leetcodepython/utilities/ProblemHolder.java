package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.machinelearningforsmallbusiness.leetcodepython.R;

import java.util.HashMap;


// NOT YET HOOKED UP TO BE USED BY MAIN ACTIVITY //

public class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView mDifficultyIcon;
    private final TextView mProblemName;

    private HashMap<String, String> problem;
    private Context context;

    public ProblemHolder(Context context, View itemView) {

        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.mDifficultyIcon = (ImageView) itemView.findViewById(R.id.iv_difficulty_icon);
        this.mProblemName = (TextView) itemView.findViewById(R.id.tv_name);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindProblem(HashMap<String, String> problem) {

        // 4. Bind the data to the ViewHolder
        this.problem = problem;
        this.mProblemName.setText(problem.get("name"));
        this.mDifficultyIcon.setImageResource(Integer.getInteger(problem.get("icon")));
    }

    @Override
    public void onClick(View v) {

        // 5. Handle the onClick event for the ViewHolder
        if (this.problem != null) {
            // TODOO
        }
    }
}