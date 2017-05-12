package com.example.simsu451.androidprojekt.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.R;

/**
 * Created by simsu451 on 12/05/17.
 */

public class ChatsAdapter extends ArrayAdapter{


    public ChatsAdapter(Context context, ListView listView, SwipeRefreshLayout swipeRefreshLayout) {
        super(context, R.layout.activity_chats);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bundle bundle = new Bundle();
        //bundle.putString("friend", friend);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        getContext().startActivity(intent);
        intent.putExtras(bundle);
        return convertView;
    }

}
