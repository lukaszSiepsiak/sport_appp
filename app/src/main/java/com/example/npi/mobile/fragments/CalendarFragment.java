package com.example.npi.mobile.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.MainActivity;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.Account;
import com.example.npi.mobile.json.Trainer;
import com.example.npi.mobile.json.Visit;
import com.example.npi.mobile.register.activities.TrainerRegister;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private Button startHourButton, endHourButton, bookUpButton, rezervationButton;
    int startHour, startMinute, endHour, endMinute;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartHourS() {
        return startHourS;
    }

    public void setStartHourS(String startHourS) {
        this.startHourS = startHourS;
    }

    public String getEndHourS() {
        return endHourS;
    }

    public void setEndHourS(String endHourS) {
        this.endHourS = endHourS;
    }

    String date, startHourS, endHourS, status;
    TextView startHourTextView;
    TextView endHourTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        startHourButton = view.findViewById(R.id.startHourButton);
        endHourButton = view.findViewById(R.id.endHourButton);
        bookUpButton = view.findViewById(R.id.bookUpButton);
        rezervationButton = view.findViewById(R.id.rezervationList);
        startHourTextView = view.findViewById(R.id.startHourTextView);
        endHourTextView = view.findViewById(R.id.endHourTextView);
        status = "oczekująca";

        rezervationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VisitFragment.class);
                startActivity(intent);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/" + month + 1 + "/" + year;
                Toast.makeText(getContext(), date, Toast.LENGTH_LONG).show();
            }
        });

        startHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                startHour = c.get(Calendar.HOUR_OF_DAY);
                startMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        startHourS = hourOfDay + ":" + minute;
                        startHourTextView.setText(startHourS);
                    }
                }, startHour, startMinute, false);
                timePickerDialog.show();
            }
        });

        endHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                endHour = c.get(Calendar.HOUR_OF_DAY);
                endMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        endHourS = hourOfDay + ":" + minute;
                        endHourTextView.setText(endHourS);
                    }
                }, endHour, endMinute, false);
                timePickerDialog.show();
            }
        });

        return view;

    }


    private class VisitPostTask extends AsyncTask<Void, Void, Void> {
        private String url, trainerUrl;
        private HttpEntity visitEntity;
        private HttpEntity trainerEntity;
        private HttpHeaders headers, trainerHeaders;
        private RestTemplate restTemplate;
        private ResponseEntity<Visit> visitResponseEntity;
        private ResponseEntity<Trainer> trainerResponseEntity;
        private Trainer trainer;
        private Visit visit;
        private Context context;

        public VisitPostTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            url = AssetsHelper.getServerUrl(context) + "/visit";
            visit = getVisit();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }


        @Override
        protected Void doInBackground(Void... voids) {
            visitEntity = new HttpEntity(visit, headers);
            visitResponseEntity = restTemplate.exchange(url, HttpMethod.POST, visitEntity, Visit.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (visitResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                Toast.makeText(getContext(), "Wizyta zapisana !", Toast.LENGTH_LONG).show();
            }
        }

        private Visit getVisit() {
            Visit visit = new Visit();
//            visit.setId(this);
            visit.setDate(date);
            visit.setStatus(status);
            visit.setTimeStart(startHourTextView.getText().toString());
            visit.setTimeEnd(endHourTextView.getText().toString());
            visit.setTrainer(trainer);
//        }
            return visit;

        }
    }
}
