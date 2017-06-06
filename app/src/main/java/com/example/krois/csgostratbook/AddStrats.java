package com.example.krois.csgostratbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by krois on 06.06.2017.
 */

public class AddStrats extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private Request request;
    private static final String TAG = "STRATS";
    final String url = "http://glehr.at/api.php/strats";
    private JSONArray jarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addstrats);

        final Button button = (Button) findViewById(R.id.addstrat);
        final EditText eHeading = (EditText) findViewById(R.id.heading);
        final EditText eSummary = (EditText) findViewById(R.id.summary);
        final EditText eStrat = (EditText) findViewById(R.id.strat);
        final EditText eLink = (EditText) findViewById(R.id.link);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> params = new HashMap<String, String>();
                params.put("head", eHeading.getText().toString());
                params.put("summary", eSummary.getText().toString());
                params.put("body", eStrat.getText().toString());
                params.put("map", "de_inferno");
                params.put("bild", eLink.getText().toString());

                JSONObject parameter = new JSONObject(params);

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, parameter.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("content-type", "application/json; charset=utf-8")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("response", call.request().body().toString());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("response", response.body().string());
                        Intent intent = new Intent();
                        intent.setClass(AddStrats.this, ListStrats.class);
                        startActivity(intent);
                    }

                });

            }
        });
    }
}
