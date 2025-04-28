package com.example.weatherapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListAdeptar extends AbstractItem <ListAdeptar , ListAdeptar.ViewHolder>{
   Entity entity;

    public ListAdeptar(Entity entity) {
        this.entity = entity;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 102;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_list2;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.citytv.setText(entity.getCity());
        holder.condition.setText(entity.getCondition());
        holder.temp.setText(entity.getTempreture() + "°C");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(new Date(entity.getLastUpdateTime()));
        holder.time.setText(formattedTime);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView citytv , temp , condition , time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            citytv = itemView.findViewById(R.id.cityTxt);
            temp = itemView.findViewById(R.id.tempTxt);
            condition = itemView.findViewById(R.id.weatherTxt);
            time =itemView.findViewById(R.id.dateTxt);
        }
    }
}
