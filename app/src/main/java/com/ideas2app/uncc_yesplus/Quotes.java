package com.ideas2app.uncc_yesplus;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 6/15/2018.
 */

public class Quotes {
    public String key, quote, author;

    public Quotes() {
    }

    public Quotes(String quoteId, String quote, String author) {
        this.key = quoteId;
        this.quote = quote;
        this.author = author;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("quote", quote);
        result.put("author", author);
        result.put("key", key);
        return result;
    }

    @Override
    public String toString() {
        return "Quotes{" +
                "key='" + key + '\'' +
                ", quote='" + quote + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
