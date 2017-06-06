package com.example.krois.csgostratbook;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by krois on 02.06.2017.
 */

public class DisplayStrats extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private Request request;
    private static final String TAG = "STRATS";
    private JSONArray jarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaystrats);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Intent intent = new Intent(getIntent());
        String listfield = intent.getStringExtra("listfield");
        String [] seperated = listfield.trim().split(":");
        Log.e("sep", seperated[0]);

        final String url = "http://glehr.at/api.php/strats/" + seperated[0];
        Log.e("url", url);

        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            //Passiert, wenn keine Response kommt
            @Override
            public void onFailure(Call call, IOException e) {

                if (e.getMessage() != null) {
                    Log.i(TAG, e.getMessage());
                }
            }

            //Passiert, wenn eine Response zurückkommt
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected Code " + response);
                } else {
                    final String responseData = response.body().string();
                    Log.i(TAG, responseData);

                   DisplayStrats.this.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {

                           JSONObject jobj = null;
                           try {
                               jobj = new JSONObject(responseData);
                               Integer id_final = jobj.getInt("id");
                               String head = jobj.getString("head");
                               String summary = jobj.getString("summary");
                               String body = jobj.getString("body");
                               String map = jobj.getString("map");
                               String bild = jobj.getString("bild");

                               Log.e("jobj", jobj.toString());

                               TextView tv_head = (TextView) findViewById(R.id.head);
                               TextView tv_summary = (TextView) findViewById(R.id.summary);
                               TextView tv_body = (TextView) findViewById(R.id.body);
                               ImageView mapView = (ImageView) findViewById(R.id.mapview);
                               //TextView tv_map = (TextView) findViewById(R.id.map);

                               tv_head.setText(head + " for: " + map);
                               tv_summary.setText(summary);
                               tv_summary.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                   tv_body.setText(Html.fromHtml(body,Html.FROM_HTML_MODE_LEGACY));
                               }
                               else {
                                   tv_body.setText(Html.fromHtml(body));
                               }
                               if(!bild.isEmpty()){
                                   Picasso.with(getApplicationContext()).load(bild).fit().centerInside().into(mapView);
                                   PhotoViewAttacher pAttacher;
                                   pAttacher = new PhotoViewAttacher(mapView);
                                   pAttacher.update();
                               }
                               else{
                                   Toast.makeText(DisplayStrats.this, "No Picture available", Toast.LENGTH_LONG).show();
                               }

                               //tv_map.setText(map);




                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                   });
                }
            }
        });
    }
    }


