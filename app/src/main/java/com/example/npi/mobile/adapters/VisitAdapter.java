package com.example.npi.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.npi.mobile.R;
import com.example.npi.mobile.fragments.CalendarFragment;
import com.example.npi.mobile.json.Trainer;
import com.example.npi.mobile.json.Visit;

import java.util.List;

public class VisitAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Visit> visitList;

    public VisitAdapter(Context context, List<Visit> visitList) {
        this.context = context;
        this.visitList = visitList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return visitList.size();
    }

    @Override
    public Visit getItem(int position) {
        return visitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.visit_list_item, null);
            TextView coach = view.findViewById(R.id.coachName);
            TextView status = view.findViewById(R.id.status);
            TextView data = view.findViewById(R.id.data);
            TextView startHour = view.findViewById(R.id.startHour);
            TextView endHour = view.findViewById(R.id.endHour);
            Visit current = visitList.get(position);
            coach.setText(current.getTrainer().getFirstName() + " " + current.getTrainer().getLastName());
            status.setText(current.getStatus());
            data.setText(current.getDate());
            startHour.setText(current.getTimeStart());
            endHour.setText(current.getTimeEnd());
        }
        return view;
    }
}
