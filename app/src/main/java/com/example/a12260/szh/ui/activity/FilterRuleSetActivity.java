package com.example.a12260.szh.ui.activity;

import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.FilterRule;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.SharedPreferManager;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 12260
 */
public class FilterRuleSetActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_app_filter);

        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
        List<String> packs = apps.stream().filter(x -> (x.flags & ApplicationInfo.FLAG_SYSTEM) <= 0).map(x -> x.packageName).collect(Collectors.toList());
        System.out.println("总共有:" + packs.size());
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.loading);
        new InitRecyclerViewTask().execute(packs);
    }

    class InitRecyclerViewTask extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected Void doInBackground(List<String>... lists) {
            recyclerView.setAdapter(new AppAdapter(SharedPreferManager.getFilter(), lists[0]));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private Switch aSwitch;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemicon);
            textView = itemView.findViewById(R.id.title);
            aSwitch = itemView.findViewById(R.id.itemcheck);
        }
    }

    static class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {
        private FilterRule filterRule;
        private List<String> packageApps;

        public AppAdapter(FilterRule filterRule, List<String> packageApps1) {
            this.filterRule = filterRule;
            this.packageApps = packageApps1;
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
            return new AppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            String pack = packageApps.get(position);
            holder.textView.setText(MyApplication.getAppName(pack));
            holder.imageView.setImageDrawable(MyApplication.getAppIcon(pack));
        }

        @Override
        public int getItemCount() {
            return packageApps.size();
        }
    }

}
