package com.wecu.widgetdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SelectWidgetActivity extends AppCompatActivity {

    private RecyclerView mRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_widget);
        initView();
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        WidgetHelperV16 widgetHelperV16 = new WidgetHelperV16(this);
        widgetHelperV16.getAllWidgets();
        mRv.setAdapter(new RvAdapter(widgetHelperV16.getWidgetModel()));
    }

    private class RvAdapter extends RecyclerView.Adapter<RvHolder> {

        WidgetModel widgetModel;
        ArrayList<PackageItemInfo> name = new ArrayList<>();
        ArrayList<ArrayList<Object>> nameItem = new ArrayList<>();

        public RvAdapter(WidgetModel widgetModel) {
            this.widgetModel = widgetModel;
            Set<Map.Entry<PackageItemInfo, ArrayList<Object>>> set = widgetModel.mWidgetsList.entrySet();
            for (Map.Entry<PackageItemInfo, ArrayList<Object>> entry : set) {
                PackageItemInfo key = entry.getKey();
                ArrayList<Object> value = entry.getValue();
                name.add(key);
                nameItem.add(value);
            }
        }

        @Override
        public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RvHolder(LayoutInflater.from(SelectWidgetActivity.this).inflate(R.layout.rv_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RvHolder holder, int position) {
            holder.mIcon.setImageResource(R.mipmap.ic_launcher_round);
            holder.mContent.removeAllViews();
            holder.mTitle.setText(name.get(position).packageName);
            for (int i = 0; i < nameItem.get(position).size(); i++) {
                TextView t = new TextView(SelectWidgetActivity.this);
                t.setText(((WidgetProviderInfo) nameItem.get(position).get(i)).label);
                holder.mContent.addView(t);
            }

        }

        @Override
        public int getItemCount() {
            return widgetModel.mWidgetsList.size();
        }
    }

    private static class RvHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public LinearLayout mContent;
        public TextView mTitle;

        public RvHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mContent = (LinearLayout) itemView.findViewById(R.id.content);
        }
    }
}
