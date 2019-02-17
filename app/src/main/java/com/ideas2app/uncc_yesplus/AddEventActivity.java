package com.ideas2app.uncc_yesplus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    Spinner spinner;
    EditText etEventTitle, etEventDescription, etEventPlace;
    static EditText etEventDate, etEventStartTime, etEventEndTime;
    String eventTitleValue, eventDescValue, eventDateValue, eventStartTimeValue, eventEndTimeValue, dropDownValue, eventPlaceValue;
    Button btnEventAdd, btnEventCancel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitle("");
        //toolbar.setSubtitle("");
        //getSupportActionBar().setLogo(R.drawable.yesplus_logo_top);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventStartTime = findViewById(R.id.etEventStartTime);
        etEventEndTime = findViewById(R.id.etEventEndTime);
        etEventPlace = findViewById(R.id.etEventPlace);

        spinner = (Spinner) findViewById(R.id.spinner);
        String[] countryArray = {"UNC Charlotte", "UNC Chapel-Hill", "University of Texas Dallas", "Northeastern University", "Carnegie Mellon University"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,R.id.textViewss, countryArray);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dropDownValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        etEventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StartTimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        etEventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EndTimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        btnEventAdd = findViewById(R.id.btnAddEvent);
        btnEventCancel = findViewById(R.id.btnEventCancel);

        btnEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEventAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error=false;
                try{
                    if(etEventTitle.getText().length() > 1){
                        eventTitleValue = etEventTitle.getText().toString();
                    } else{
                        error = true;
                        etEventTitle.setError("Required");
                    }

                    if(etEventDescription.getText().length() > 1){
                        eventDescValue = etEventDescription.getText().toString();
                    } else{
                        error = true;
                        etEventDescription.setError("Required");
                    }

                    if(etEventDate.getText().length() > 1){
                        eventDateValue = etEventDate.getText().toString();
                    } else{
                        error = true;
                        etEventDate.setError("Required");
                    }

                    if(etEventStartTime.getText().length() > 1){
                        eventStartTimeValue = etEventStartTime.getText().toString();
                    } else{
                        error = true;
                        etEventStartTime.setError("Required");
                    }

                    if(etEventEndTime.getText().length() > 1){
                        eventEndTimeValue = etEventEndTime.getText().toString();
                    } else{
                        error = true;
                        etEventEndTime.setError("Required");
                    }

                    if(etEventPlace.getText().length() > 1){
                        eventPlaceValue = etEventPlace.getText().toString();
                    } else{
                        error = true;
                        etEventPlace.setError("Required");
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

                if(!error){
                        if(isConnected()){
                            progressDialog = new ProgressDialog(AddEventActivity.this);
                            progressDialog.getWindow().setBackgroundDrawable(new
                                    ColorDrawable(android.graphics.Color.TRANSPARENT));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progressdialog);

                            Event event = new Event();
                            event.eTitle = eventTitleValue;
                            event.eDescription = eventDescValue;
                            event.eDate = eventDateValue;
                            event.eStartTime = eventStartTimeValue;
                            event.eEndTime = eventEndTimeValue;
                            event.ePlace = eventPlaceValue;
                            event.eUniversity = dropDownValue;

                            addEvent(event);
                            Intent i = new Intent(AddEventActivity.this, EventActivity.class);
                            finish();
                            startActivity(i);
                        } else{
                            Toast.makeText(AddEventActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                }else{
                    Toast.makeText(AddEventActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void addEvent(Event event){
        String key = myRef.child("allEvents").push().getKey();
        event.eId= key;
        Map<String, Object> threadValues = event.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.child("allEvents").updateChildren(childUpdates);
        Toast.makeText(AddEventActivity.this, "Event Added", Toast.LENGTH_SHORT).show();
    }

    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String amPM = (hourOfDay>12)?"PM":"AM";
            hourOfDay = hourOfDay % 12;
            etEventStartTime.setText(hourOfDay+ ":"+minute+" "+amPM);
        }
    }

    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String amPM = (hourOfDay>12)?"PM":"AM";
            hourOfDay = hourOfDay % 12;
            etEventEndTime.setText(hourOfDay+ ":"+minute+" "+amPM);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            etEventDate.setText(month+1+"/"+day+"/"+year);
        }
    }

}
