package com.example.simsu451.androidprojekt;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class WallAdapter extends ArrayAdapter {
    public WallAdapter(Context context, int resource) {
        super(context, resource);
    }
}
