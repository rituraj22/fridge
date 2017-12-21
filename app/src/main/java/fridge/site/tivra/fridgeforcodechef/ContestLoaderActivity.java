package fridge.site.tivra.fridgeforcodechef;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContestLoaderActivity extends AppCompatActivity {

    String apiurl,code;
    RequestQueue queue;

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        swipeRefreshLayout=findViewById(R.id.contest_swipe_refresh_layout);
        findViewById(R.id.contest_toolbar).setVisibility(View.GONE);
        findViewById(R.id.full_contest_download).setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        queue= Volley.newRequestQueue(this);
        code=getIntent().getExtras().getString("code");
        apiurl="https://www.codechef.com/api/contests/"+code;
        File file=new File(getFilesDir(),code+".contest");
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, apiurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject time=response.getJSONObject("time");
                    String contestName=response.getString("name");
                    long start=time.getLong("start");
                    long end=time.getLong("end");
                    Date startDate=new Date(start*1000);
                    Date endDate=new Date(end*1000);
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    String sDate=simpleDateFormat.format(startDate);
                    String eDate=simpleDateFormat.format(endDate);
                    Intent i=new Intent(getApplicationContext(),ContestActivity.class);
                    swipeRefreshLayout.setRefreshing(false);
                    i.putExtra("code", code);
                    i.putExtra("name", contestName);
                    i.putExtra("start",sDate);
                    i.putExtra("end",eDate);
                    startActivity(i);
                    finish();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    StaticHelper.openBrowser("https://www.codechef.com/"+code,getApplicationContext());
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading",Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(jsonObjectRequest);
            }
        });

        if(file.exists()) {
            Intent i=new Intent(getApplicationContext(),ContestActivity.class);
            swipeRefreshLayout.setRefreshing(false);
            FileInputStream fileInputStream= null;
            try {
                fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
            i.putExtra("code", code);
            i.putExtra("name", bufferedReader.readLine());
            i.putExtra("start",bufferedReader.readLine());
            i.putExtra("end",bufferedReader.readLine());
            fileInputStream.close();
            bufferedReader.close();
            startActivity(i);
            finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                queue.add(jsonObjectRequest);
            } catch (IOException e) {
                e.printStackTrace();
                queue.add(jsonObjectRequest);
            }
        }
        else {
            queue.add(jsonObjectRequest);
        }
    }
}
