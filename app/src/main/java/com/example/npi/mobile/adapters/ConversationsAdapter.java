package com.example.npi.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.npi.mobile.R;
import com.example.npi.mobile.json.AccountData;

import java.util.List;

public class ConversationsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<AccountData> accountDataList;

    public ConversationsAdapter(Context context, List<AccountData> accountDataList) {
        this.context = context;
        this.accountDataList = accountDataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return accountDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return accountDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.trainer_list_item, null);
        }
        TextView firstName = view.findViewById(R.id.firstName);
        TextView lastName = view.findViewById(R.id.lastName);
        AccountData current = accountDataList.get(i);
        firstName.setText(current.getFirstName());
        lastName.setText(current.getLastName());
        return view;
    }
}
