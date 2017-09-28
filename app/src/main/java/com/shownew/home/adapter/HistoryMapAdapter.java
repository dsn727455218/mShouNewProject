package com.shownew.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.map.HistoryTrajectoryActivity;
import com.shownew.home.module.entity.HistoryMapPointEntity;

import java.util.ArrayList;

/**历史停车点
 * Created by WP on 2017/7/20.
 */

public class HistoryMapAdapter extends RecyclerView.Adapter<HistoryMapAdapter.HistoryMapViewHolder> {

    private ArrayList<HistoryMapPointEntity> historyMapPointEntities;
    private HistoryTrajectoryActivity context;
    private final static String ONE_HTML = "<small><font color=#727272>%s</font></small><br/><big><font color=#3681f1>%s</font></big>";
    private final static String HTML = "<big><font color=#3681f1>%s</font></big>";

    public HistoryMapAdapter(ArrayList<HistoryMapPointEntity> historyMapPointEntities, HistoryTrajectoryActivity context) {
        this.historyMapPointEntities = historyMapPointEntities;
        this.context = context;
    }

    @Override
    public HistoryMapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryMapViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_history_map_items, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryMapViewHolder holder, int position) {
        final HistoryMapPointEntity historyMapPointEntity = historyMapPointEntities.get(position);
        if (null == historyMapPointEntity)
            return;
        holder.show_more.setVisibility(View.GONE);
        if (position == 0) {
            holder.show_more.setVisibility(View.VISIBLE);
            holder.show_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context != null) {
                        context.showMore();
                    }
                }
            });
            holder.show_more.setText(historyMapPointEntity.isShow() ? "收起" : "显示更多");
            holder.content_tv.setText(Html.fromHtml(String.format(ONE_HTML, "最近停车", historyMapPointEntity.getGText())));
            holder.logisitice_circle.setImageResource(R.drawable.target);
        } else {
            holder.content_tv.setText(Html.fromHtml(String.format(HTML, historyMapPointEntity.getGText())));
            holder.logisitice_circle.setImageResource(R.drawable.gray_target);
        }
        holder.content_time.setText(historyMapPointEntity.getGDate());
        holder.content_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != context)
                    context.clickItem(historyMapPointEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyMapPointEntities.size();
    }

    class HistoryMapViewHolder extends RecyclerView.ViewHolder {
        ImageView logisitice_circle;
        TextView content_tv;
        TextView content_time;
        TextView show_more;

        public HistoryMapViewHolder(View convertView) {
            super(convertView);
            logisitice_circle = (ImageView) convertView.findViewById(R.id.logisitice_circle);
            content_tv = (TextView) convertView.findViewById(R.id.content_tv);
            content_time = (TextView) convertView.findViewById(R.id.content_time);
            show_more = (TextView) convertView.findViewById(R.id.show_more);
        }
    }

}
