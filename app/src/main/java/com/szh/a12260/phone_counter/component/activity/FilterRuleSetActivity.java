package com.szh.a12260.phone_counter.component.activity;

import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.szh.a12260.phone_counter.R;
import com.szh.a12260.phone_counter.model.FilterRule;
import com.szh.a12260.phone_counter.utils.MyApplication;
import com.szh.a12260.phone_counter.utils.SharedPreferManager;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 12260
 */
public class FilterRuleSetActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Switch bgSwitch;
    FilterRule filterRule;
    List<String> packs;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_app_filter);
        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
        packs = apps.stream().filter(x -> (x.flags & ApplicationInfo.FLAG_SYSTEM) <= 0).map(x -> x.packageName).collect(Collectors.toList());
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.loading);
        bgSwitch = findViewById(R.id.backgroundSetting);
        filterRule = SharedPreferManager.getFilter();
        bgSwitch.setChecked(filterRule.isSelectPartOrAll());
        bgSwitch.setOnCheckedChangeListener(this);
        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new InitRecyclerViewTask().execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        filterRule.setSelectPartOrAll(isChecked);
        filterRule.getPackages().clear();
        SharedPreferManager.saveFilterRule(filterRule);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    class InitRecyclerViewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... lists) {
            recyclerView.setAdapter(new AppAdapter(filterRule, packs));

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
            if (filterRule.isSelectPartOrAll()) {
                holder.aSwitch.setChecked(true);
                if (filterRule.getPackages().contains(pack)) {
                    holder.aSwitch.setChecked(false);
                }
            } else {
                holder.aSwitch.setChecked(false);
                if (filterRule.getPackages().contains(pack)) {
                    holder.aSwitch.setChecked(true);
                }
            }
            holder.aSwitch.setOnCheckedChangeListener((x, y) -> {
                if (filterRule.isSelectPartOrAll() != y) {
                    filterRule.getPackages().add(pack);
                } else {
                    filterRule.getPackages().remove(pack);
                }
                SharedPreferManager.saveFilterRule(filterRule);
                System.out.println(filterRule.getPackages().size());
            });
        }

        @Override
        public int getItemCount() {
            return packageApps.size();
        }
    }

}
