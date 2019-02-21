package com.example.a12260.szh.ui;

import android.view.View;

import com.example.a12260.szh.R;

import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TwoChartViewHolder extends RecyclerView.ViewHolder {
    public View chartUp;
    public View chartDown;
    public TwoChartViewHolder(@NonNull View itemView) {
        super(itemView);
        chartUp = itemView.findViewById(R.id.constraintLayout3);
        chartDown = itemView.findViewById(R.id.constraintLayout2);
    }

}
