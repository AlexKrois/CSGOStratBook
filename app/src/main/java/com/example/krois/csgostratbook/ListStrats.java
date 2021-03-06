package com.example.krois.csgostratbook;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListStrats extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private Request request;
    private static final String TAG = "STRATS";
    final String url = "http://glehr.at/api.php/strats";
    private JSONArray jarray;
    private SwipeRefreshLayout swipeContainer;


    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_strats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Swag", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                Intent intent =  new Intent(ListStrats.this, AddStrats.class);
                intent.putExtra("test", url);
                startActivity(intent);

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


        //Aufgehts b o i

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

                    ListStrats.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //jarray = new JSONArray(responseData);
                                final JSONObject jobj = new JSONObject(responseData);
                                final ArrayList<String> namelist = new ArrayList<String>();
                                final ListView listView = (ListView) findViewById(R.id.list);
                                final ImageView cView = (ImageView) findViewById(R.id.thumbnail);

                                Log.e("jarray", jobj.toString());

                                JSONObject strats = jobj.getJSONObject("strats");
                                Log.e("strats", strats.toString());


                                JSONArray records = strats.getJSONArray("records");

                                for(int i = 0; i < records.length(); i++){
                                    try { Thread.sleep(100);}
                                    catch (InterruptedException e) { e.printStackTrace(); }

                                    Log.e("records", records.get(i).toString());
                                    Integer id = 0;
                                    String[] seperated = records.get(i).toString().split(",");
                                    id = Integer.valueOf(seperated[0].replace("[","").trim());
                                    Log.e("idlist", String.valueOf(id));

                                    //Single call

                                    final String url_single = "http://glehr.at/api.php/strats/" + id;

                                    okHttpClient = new OkHttpClient();
                                    request = new Request.Builder().url(url_single).build();

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

                                                ListStrats.this.runOnUiThread(new Runnable() {
                                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            final JSONObject jobj = new JSONObject(responseData);
                                                            Log.e("jobj",jobj.toString());
                                                            final Integer id_final = jobj.getInt("id");
                                                            Log.e("finalid", String.valueOf(id_final));
                                                            String head = jobj.getString("head");
                                                            String summary = jobj.getString("summary");
                                                            String body = jobj.getString("body");
                                                            String map = jobj.getString("map");
                                                            String bild = jobj.getString("bild");

                                                            namelist.add(id_final + ": " + map + " - " + summary + " - " + head);
                                                            Log.e("namelist", namelist.toString());

                                                            Log.e("namelist2", namelist.toString());

                                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                                        int position, long id) {

                                                                    Intent intent = new Intent(ListStrats.this, DisplayStrats.class);
                                                                    intent.putExtra("listfield", namelist.get(position));

                                                                    startActivity(intent);

                                                                }
                                                            });

                                                            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(ListStrats.this, R.layout.list, R.id.textview, namelist);
                                                            listView.setAdapter(mArrayAdapter);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


                                                    }
                                                });
                                            }
                                        }
                                    });
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_strats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
