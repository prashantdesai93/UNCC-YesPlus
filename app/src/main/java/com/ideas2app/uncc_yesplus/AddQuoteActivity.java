package com.ideas2app.uncc_yesplus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddQuoteActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    EditText etAuthor, etQuote;
    Button btnAddQuote, btnQuoteCancel;
    String authorValue, quoteValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        etAuthor = findViewById(R.id.etAuthor);
        etQuote = findViewById(R.id.etQuote);
        btnAddQuote = findViewById(R.id.btnAddQuote);
        btnQuoteCancel = findViewById(R.id.btnQuoteCancel);

        btnQuoteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                try {
                    if (etAuthor.getText().length() > 1) {
                        authorValue = etAuthor.getText().toString();
                    } else {
                        error = true;
                        etAuthor.setError("Required");
                    }

                    if (etQuote.getText().length() > 1) {
                        quoteValue = etQuote.getText().toString();
                    } else {
                        error = true;
                        etQuote.setError("Required");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!error){
                    if(isConnected()){
                        Quotes quotes = new Quotes();
                        quotes.author = authorValue;
                        quotes.quote = quoteValue;


                        addQuote(quotes);
                        Intent i = new Intent(AddQuoteActivity.this, EventActivity.class);
                        finish();
                        startActivity(i);
                    } else{
                        Toast.makeText(AddQuoteActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddQuoteActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
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

    public void addQuote(Quotes quotes){
        String key = myRef.child("allQuotes").push().getKey();
        quotes.key= key;
        Map<String, Object> threadValues = quotes.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.child("allQuotes").updateChildren(childUpdates);
        Toast.makeText(AddQuoteActivity.this, "Quote Added", Toast.LENGTH_SHORT).show();
    }
}
