package com.example.npi.mobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


//public class MultiSpinner extends android.support.v7.widget.AppCompatSpinner implements
//        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {
//
//    public List<String> getItems() {
//        return items;
//    }
//
//    public void setItems(List<String> items) {
//        this.items = items;
//    }
//
//    private List<String> items;
//    private boolean[] selected;
//    private String defaultText;
//    private MultiSpinnerListener listener;
//
//    public MultiSpinner(Context context) {
//        super(context);
//    }
//
//    public MultiSpinner(Context arg0, AttributeSet arg1) {
//        super(arg0, arg1);
//    }
//
//    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
//        super(arg0, arg1, arg2);
//    }
//
//    @Override
//    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//        if (isChecked)
//            selected[which] = true;
//        else
//            selected[which] = false;
//    }
//
//    @Override
//    public void onCancel(DialogInterface dialog) {
//        // refresh text on spinner
//        StringBuffer spinnerBuffer = new StringBuffer();
//        boolean someUnselected = false;
//        for (int i = 0; i < items.size(); i++) {
//            if (selected[i] == true) {
//                spinnerBuffer.append(items.get(i));
//                spinnerBuffer.append(", ");
//            } else {
//                someUnselected = true;
//            }
//        }
//        String spinnerText;
//        if (someUnselected) {
//            spinnerText = spinnerBuffer.toString();
//            if (spinnerText.length() > 2)
//                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
//        } else {
//            spinnerText = defaultText;
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item,
//                new String[] { spinnerText });
//        setAdapter(adapter);
//        listener.onItemsSelected(selected);
//    }
//
//    @Override
//    public boolean performClick() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setMultiChoiceItems(
//                items.toArray(new CharSequence[items.size()]), selected, this);
//        builder.setPositiveButton(android.R.string.ok,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        builder.setOnCancelListener(this);
//        builder.show();
//        return true;
//    }
//
//    public void setItems(List<String> items, String allText,
//                         MultiSpinnerListener listener) {
//        this.items = items;
//        this.defaultText = allText;
//        this.listener = listener;
//
//        // all selected by default
//        selected = new boolean[items.size()];
//        for (int i = 0; i < selected.length; i++)
//            selected[i] = true;
//
//        // all text on the spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item, new String[] { allText });
//        setAdapter(adapter);
//    }
//
//    public interface MultiSpinnerListener {
//        public void onItemsSelected(boolean[] selected);
//    }
//}
public class MultiSpinner extends android.support.v7.widget.AppCompatSpinner {
    private CharSequence[] entries;
    private boolean[] selected;
    private MultiSpinnerListener listener;
    private List<String> items;
    private String defaultText;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        items = new ArrayList<>();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSpinner);
        entries = a.getTextArray(R.styleable.MultiSpinner_android_entries);
        if (entries != null) {
            selected = new boolean[entries.length];
        }
        a.recycle();
    }

    private DialogInterface.OnMultiChoiceClickListener mOnMultiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    };

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            StringBuffer spinnerBuffer = new StringBuffer();
            for (int i = 0; i < entries.length; i++) {
                if (selected[i]) {
                    spinnerBuffer.append(entries[i]);
                    spinnerBuffer.append(", ");
                }
            }

            if (spinnerBuffer.length() > 2) {
                spinnerBuffer.setLength(spinnerBuffer.length() - 2);
            }

            // display new text
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{spinnerBuffer.toString()});
            setAdapter(adapter);

            if (listener != null) {
                listener.onItemsSelected(selected);
            }

            dialog.dismiss();
        }
    };

    public List<String> getItems() {
        items.clear();
        for (int i = 0; i < entries.length; i++) {
            if (selected[i]) {
                items.add(entries[i].toString());
            }
        }
        return items;
    }

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setMultiChoiceItems(entries, selected, mOnMultiChoiceClickListener)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .show();
        return true;
    }

    public void setMultiSpinnerListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}

//    public void setItems(
//            List<String> items,
//            List<String> itemValues,
//            String selectedList,
//            String allText,
//            MultiSpinnerListener listener) {
//        this.items = items;
//        this.defaultText = allText;
//        this.listener = listener;
//
//        String spinnerText = allText;
//
//        // Set false by default
//        selected = new boolean[itemValues.size()];
//        for (int j = 0; j < itemValues.size(); j++)
//            selected[j] = false;
//
//        if (selectedList != null) {
//            spinnerText = "";
//            // Extract selected items
//            String[] selectedItems = selectedList.trim().split(",");
//
//            // Set selected items to true
//            for (int i = 0; i < selectedItems.length; i++)
//                for (int j = 0; j < itemValues.size(); j++)
//                    if (selectedItems[i].trim().equals(itemValues.get(j))) {
//                        selected[j] = true;
//                        spinnerText += (spinnerText.equals("")?"":", ") + items.get(j);
//                        break;
//                    }
//        }
//
//        // Text for the spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item, new String[] { spinnerText });
//        setAdapter(adapter);
//}