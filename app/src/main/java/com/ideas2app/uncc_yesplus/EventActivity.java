package com.ideas2app.uncc_yesplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    FirebaseDatabase database;
    DatabaseReference myRefEvent, myRefQuote;
    FirebaseUser user;

    GoogleApiClient googleApiClient;
    ListView eventListView, quotesListView;
    EventAdapter eventAdapter;
    QuotesAdapter quotesAdapter;
    String TAG = "demo";
    List<Event> eventList;
    List<Quotes> quoteList;
    String pageId = "eventPage";
    FloatingActionButton fab;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    pageId = "eventPage";
                    fab.setVisibility(View.VISIBLE);
                    eventListView.setVisibility(View.VISIBLE);
                    quotesListView.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    pageId = "quotePage";
                    fab.setVisibility(View.VISIBLE);
                    quotesListView.setVisibility(View.VISIBLE);
                    eventListView.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    pageId = "settingPage";
                    fab.setVisibility(View.GONE);
                    quotesListView.setVisibility(View.GONE);
                    eventListView.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitle("");
        //toolbar.setSubtitle("");
       // getSupportActionBar().setLogo(R.drawable.yesplus_logo_top);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefEvent = database.getReference("allEvents");
        myRefQuote = database.getReference("allQuotes");

        eventListView = findViewById(R.id.lvAllEvents);
        eventList= new ArrayList<Event>();
        Log.d(TAG, "onCreate: 3");
        initData(eventList);

        quotesListView = findViewById(R.id.lvAllQuotes);
        quoteList= new ArrayList<Quotes>();
        initQuoteData(quoteList);

        ChildEventListener childEventListener = new ChildEventListener() { 
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Event event = null;

                    event = dataSnapshot.getValue(Event.class);
                    Log.d(TAG, "Event onChildAddedddd");
                        eventAdapter.add(event);


                eventAdapter.sort(new Comparator<Event>() {
                    DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    @Override
                    public int compare(Event o1, Event o2) {
                        Date a1 = new Date(o1.eDate);
                        Date a2 = new Date(o2.eDate);
                        try {
                            Date d1 = f.parse(a1.toString());
                            Date d2 = f.parse(a2.toString());
                            return d1.compareTo(d2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
                Event event = dataSnapshot.getValue(Event.class);
                Event toRemove=null;
                for(Event t : eventList){
                    if(t.eId.equals(event.eId)){
                        toRemove=t;
                        break;
                    }
                }
                if(toRemove!=null){
                    eventAdapter.remove(toRemove);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Thread onCancelled", databaseError.toException());
                Toast.makeText(EventActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

        ChildEventListener childEventListener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Quotes quote = dataSnapshot.getValue(Quotes.class);
                if(quote != null) {
                    quotesAdapter.add(quote);
                }
                quotesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
                Quotes quotes = dataSnapshot.getValue(Quotes.class);
                Quotes toRemove=null;
                for(Quotes t : quoteList){
                    if(t.key.equals(quotes.key)){
                        toRemove=t;
                        break;
                    }
                }
                if(toRemove!=null){
                    quotesAdapter.remove(toRemove);
                    quotesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Thread onCancelled", databaseError.toException());
                Toast.makeText(EventActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

        fab = findViewById(R.id.floating_action_button_fab_with_listview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if(isConnected()) {
                    if(pageId == "eventPage") {
                        Intent i = new Intent(EventActivity.this, AddEventActivity.class);
                        startActivity(i);
                    }
                    if(pageId == "quotePage") {
                        Intent i = new Intent(EventActivity.this, AddQuoteActivity.class);
                        startActivity(i);
                    }
                } else{
                    Toast.makeText(EventActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("demo", "i'm here- onclick event");
                Event event = eventList.get(position);
                Log.d(TAG, "Thread onItemClick: "+event.toString());
                if(isConnected()) {
                    Intent i = new Intent(EventActivity.this, EventDetailActivity.class);
                    i.putExtra("event", event);
                    //finish();
                    startActivity(i);
                } else{
                    Toast.makeText(EventActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRefEvent.addChildEventListener(childEventListener);
        myRefQuote.addChildEventListener(childEventListener1);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    public void initData(List<Event> events){
        eventAdapter = new EventAdapter(EventActivity.this, R.layout.event_item, events);
        eventListView.setAdapter(eventAdapter);
    }

    public void initQuoteData(List<Quotes> quotes){
        Log.d(TAG, "initQuoteData: quotesData"+ quotes.toString());
        quotesAdapter = new QuotesAdapter(EventActivity.this, R.layout.quote_item, quotes);
        quotesListView.setAdapter(quotesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.new_event) {
            if(isConnected()) {
                if(pageId == "eventPage") {
                    Intent i = new Intent(EventActivity.this, AddEventActivity.class);
                    startActivity(i);
                }
                if(pageId == "quotePage") {
                    Intent i = new Intent(EventActivity.this, AddQuoteActivity.class);
                    startActivity(i);
                }
            } else{
                Toast.makeText(EventActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            }
            return true;
        }*//*else if(id == R.id.logout){
            signOut();
        }*/

        return super.onOptionsItemSelected(item);
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

    public void signOut(){
        if(isConnected()) {
            if(googleApiClient != null){
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(EventActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                FirebaseAuth.getInstance().signOut();
            }

            Intent i = new Intent(EventActivity.this, LoginActivity.class);
            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, LoginActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("hasLoggedIn", false);
            editor.commit();
            finish();
            startActivity(i);
        } else{
            Toast.makeText(EventActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
        super.onStart();
    }
}
