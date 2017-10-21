package com.machinelearningforsmallbusiness.seemycode.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class ProblemAdapter extends RecyclerView.Adapter<ProblemHolder> {

    private final ArrayList<HashMap<String, String>> problems;
    private Context context;
    private int itemResource;

    public ProblemAdapter(Context context, int itemResource, ArrayList<HashMap<String, String>> problems) {
        this.problems = problems;
        this.context = context;
        this.itemResource = itemResource;
    }

    @Override
    public ProblemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new ProblemHolder(context, view);
    }

    @Override
    public void onBindViewHolder(ProblemHolder holder, int position) {
        HashMap<String, String> problem = problems.get(position);
        holder.bindProblem(problem);
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }
}
