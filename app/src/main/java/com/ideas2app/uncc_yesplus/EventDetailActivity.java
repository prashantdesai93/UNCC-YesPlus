package com.ideas2app.uncc_yesplus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    String TAG = "demo";
    String formattedDate=null;
    Event event;
    TextView tvEventTitleDetail, tvEventDateDetail, tvEventTimeDetail, tvEventDescriptionDetail;
    ImageButton ibAttending, ibDecline, ibTentative;
    FirebaseDatabase database;
    DatabaseReference myRef, acceptedMembersRef;
    FirebaseUser user;
    int checkUserEventStatus = 2;
    List<String> acceptedMembers = new ArrayList<String>();
    List<String> acceptedMembersFName = new ArrayList<String>();
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitle("");
        //toolbar.setSubtitle("");
        //getSupportActionBar().setLogo(R.drawable.yesplus_logo_top);



        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        if(getIntent() != null && getIntent().getExtras() != null ){
            event = (Event) getIntent().getSerializableExtra("event");
            Log.d(TAG, "onCreate: event "+event.toString());
        }

        tvEventTitleDetail = findViewById(R.id.tvEventTitleDetail);
        tvEventDateDetail = findViewById(R.id.tvEventDateDetail);
        tvEventTimeDetail = findViewById(R.id.tvEventTimeDetail);
        tvEventDescriptionDetail = findViewById(R.id.tvEventDescriptionDetail);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date(event.eDate);
        formattedDate= formatter.format(date);

        tvEventTitleDetail.setText(event.eTitle);
        tvEventDateDetail.setText(formattedDate);
        tvEventTimeDetail.setText(event.eStartTime+"-"+event.eEndTime);
        tvEventDescriptionDetail.setText(event.eDescription);

        ibAttending = findViewById(R.id.ibAttending);
        ibDecline = findViewById(R.id.ibDecline);
        ibTentative = findViewById(R.id.ibTentative);

        ibAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String key = myRef.child("allEvents").child(event.eId).child("accepted").push().getKey();
                //Quotes quotes = new Quotes();
                //quotes.key= key;
                //Map<String, Object> threadValues = quotes.toMap();
                if(isConnected()) {
                    Log.d(TAG, "onClick: in details attending " + user.getUid());
                    if (checkUserEventStatus == 2) {
                        checkUserEventStatus = 1;
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("accepted").updateChildren(childUpdates);
                        Toast.makeText(EventDetailActivity.this, "Thank You for Registration", Toast.LENGTH_SHORT).show();
                    } else if(checkUserEventStatus == -1 || checkUserEventStatus == 0){
                        checkUserEventStatus = 1;

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("accepted").updateChildren(childUpdates);

                        Map<String, Object> otherChildUpdates = new HashMap<>();
                        otherChildUpdates.put(user.getUid(), null);
                        myRef.child("allEvents").child(event.eId).child("rejected").updateChildren(otherChildUpdates);
                        myRef.child("allEvents").child(event.eId).child("tentative").updateChildren(otherChildUpdates);
                        Toast.makeText(EventDetailActivity.this, "Thank You for Registration", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventDetailActivity.this, "Event already Accepted", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(EventDetailActivity.this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    Log.d(TAG, "onClick: in details decline " + user.getUid());
                    if (checkUserEventStatus == 2) {
                        checkUserEventStatus = 0;
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("rejected").updateChildren(childUpdates);
                        Toast.makeText(EventDetailActivity.this, "Event Declined", Toast.LENGTH_SHORT).show();
                    } else if(checkUserEventStatus == -1 || checkUserEventStatus == 1){
                        checkUserEventStatus = 0;

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("rejected").updateChildren(childUpdates);

                        Map<String, Object> otherChildUpdates = new HashMap<>();
                        otherChildUpdates.put(user.getUid(), null);
                        myRef.child("allEvents").child(event.eId).child("accepted").updateChildren(otherChildUpdates);
                        myRef.child("allEvents").child(event.eId).child("tentative").updateChildren(otherChildUpdates);
                        Toast.makeText(EventDetailActivity.this, "Event Declined", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventDetailActivity.this, "Event already Declined", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(EventDetailActivity.this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibTentative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    Log.d(TAG, "onClick: in details attending " + user.getUid());
                    if (checkUserEventStatus == 2) {
                        checkUserEventStatus = -1;
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("tentative").updateChildren(childUpdates);
                        Toast.makeText(EventDetailActivity.this, "Event Tentatively Accepted", Toast.LENGTH_SHORT).show();
                    } else if(checkUserEventStatus == 1 || checkUserEventStatus == 0){
                        checkUserEventStatus = -1;

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(), user.getUid());
                        myRef.child("allEvents").child(event.eId).child("tentative").updateChildren(childUpdates);

                        Map<String, Object> otherChildUpdates = new HashMap<>();
                        otherChildUpdates.put(user.getUid(), null);
                        myRef.child("allEvents").child(event.eId).child("accepted").updateChildren(otherChildUpdates);
                        myRef.child("allEvents").child(event.eId).child("rejected").updateChildren(otherChildUpdates);
                        Toast.makeText(EventDetailActivity.this, "Event Tentatively Accepted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventDetailActivity.this, "Event already Tentatively Accepted", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(EventDetailActivity.this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Accepted Members");
        listDataHeader.add("Tentative Members");
        listDataHeader.add("Declined Members");

        ChildEventListener childEventListenerUsers = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildAdded:childEventListenerUsers  "+user+ " "+acceptedMembers);

                for(int i=0; i<acceptedMembers.size(); i++){
                    if(acceptedMembers.get(i).toString().equals(user.id) ){
                        acceptedMembersFName.add(user.fName+" "+user.lName);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.child("users").addChildEventListener(childEventListenerUsers);

        ChildEventListener childEventListenerAccepted = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {


                acceptedMembers.add((String)dataSnapshot.getValue());
                Log.d(TAG, "onChildAdded: acceptedMembers "+acceptedMembers);
                /*myRef.child("users").child(dataSnapshot.getValue().toString()).child("fName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            if (snapshot.getValue() != null) {
                                try {
                                    Log.d("TAG", "naaaaaaaaaaaammmmme" + snapshot.getValue()); // your name values you will get here
                                    acceptedMembersFName.add(snapshot.getValue().toString());
                                    Log.d(TAG, "onDataChange: acceptedMembersFName "+acceptedMembersFName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("TAG", " it's null.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });*/

                /*for(int i = 0; i < acceptedMembers.size(); i++){
                    myRef.child("users").child(acceptedMembers.get(i)).child("fName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                if (snapshot.getValue() != null) {
                                    try {
                                        Log.d("TAG", "naaaaaaaaaaaammmmme" + snapshot.getValue()); // your name values you will get here
                                        acceptedMembersFName.add((String)snapshot.getValue());
                                        Log.d(TAG, "onDataChange: acceptedMembersFName "+acceptedMembersFName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("TAG", " it's null.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }*/
                listAdapter.notifyDataSetChanged();



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Thread onCancelled", databaseError.toException());
                Toast.makeText(EventDetailActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

        myRef.child("allEvents").child(event.eId).child("accepted").addChildEventListener(childEventListenerAccepted);
        // Adding child data

        List<String> tentativeMembers = new ArrayList<String>();
        /*nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");*/

        List<String> declinedMembers = new ArrayList<String>();
        declinedMembers.add("2 Guns");
        declinedMembers.add("The Smurfs 2");
        declinedMembers.add("The Spectacular Now");
        declinedMembers.add("The Canyons");
        declinedMembers.add("Europa Report");

        //acceptedMembersFName.stream().forEach(System.out::println);
        listDataChild.put(listDataHeader.get(0), acceptedMembersFName); // Header, Child data
        listDataChild.put(listDataHeader.get(1), tentativeMembers);
        listDataChild.put(listDataHeader.get(2), declinedMembers);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        for(int i=0; i < listAdapter.getGroupCount(); i++)
            expListView.expandGroup(i);


        //Log.d(TAG, "onCreate: accepted <><><><><> "+event.toString());
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
}
