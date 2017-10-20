package com.machinelearningforsmallbusiness.leetcodepython.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.machinelearningforsmallbusiness.leetcodepython.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ProblemAdapter extends ArrayAdapter<HashMap<String, String>> {

    private final ArrayList<HashMap<String, String>> problems;
    private Context context;
    private int itemResource;

    public ProblemAdapter(Context context, int itemResource, ArrayList<HashMap<String, String>> problems) {
        // 1. Initialize our adapter
        super(context, R.layout.list_item, problems);
        this.problems = problems;
        this.context = context;
        this.itemResource = itemResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 2. Have we inflated this view before?
        View itemView;
        if (convertView != null) {

            // 2a. We have so let's reuse.
            itemView = convertView;
        }
        else {

            // 2b. We have NOT so let's inflate
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(this.itemResource, parent, false);
        }

        // 3. Get the problem to appear in this item
        HashMap<String, String> problem = this.problems.get(position);
        if (problem != null) {

            // 4. Inflate the UI widgets
            ImageView mDifficultyIcon = (ImageView) itemView.findViewById(R.id.iv_difficulty_icon);
            TextView mProblemName = (TextView) itemView.findViewById(R.id.tv_name);

            // 5. Set the UI widgets with appropriate data from the problem
            mDifficultyIcon.setImageResource(Integer.parseInt(problem.get("icon")));
            mProblemName.setText(problem.get("name"));
        }

        return itemView;
    }
}
