package com.ideas2app.uncc_yesplus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hp on 6/15/2018.
 */

public class QuotesAdapter extends ArrayAdapter<Quotes> {
    List<Quotes> objects;
    Quotes quotes;
    Context context;

    public QuotesAdapter(Context context, int resource, List<Quotes> objects){
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        Log.d("demo", "QuotesAdapter: ");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("demo", "Helloooooooooooooooooo quotes");
        //AllMessageThreads allMessageThreads = getItem(position);
        quotes = getItem(position);
//        Log.d("demo", "MessageThreadAdapter : getView: t = "+t.toString());
        //Log.d("demo", "getView: "+allMessageThreads.threads.get(0).title);
        QuotesAdapter.ViewHolder viewHolder;
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.quote_item ,parent,false);
            viewHolder = new QuotesAdapter.ViewHolder();
            viewHolder.tvQuote = convertView.findViewById(R.id.tvQuote);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tvAuthor);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (QuotesAdapter.ViewHolder) convertView.getTag();
        }

        /*viewHolder.tvQuote.setText(quotes.quoteTitle);
        viewHolder.tvAuthor.setText(quotes.quoteAuthor);*/
        viewHolder.tvQuote.setText(quotes.quote);
        viewHolder.tvAuthor.setText("- "+quotes.author);
        return convertView;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private static class ViewHolder{
        TextView tvQuote;
        TextView tvAuthor;
    }

}
