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

public class QuestionLoaderActivity extends AppCompatActivity {

    String apiurl,code,qcode;
    RequestQueue queue;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        swipeRefreshLayout=findViewById(R.id.contest_swipe_refresh_layout);
        findViewById(R.id.contest_toolbar).setVisibility(View.GONE);
        findViewById(R.id.full_contest_download).setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        code=getIntent().getExtras().getString("code");
        qcode=getIntent().getExtras().getString("qcode");

        apiurl="https://www.codechef.com/api/contests/"+code+"/problems/"+qcode;
        queue= Volley.newRequestQueue(this);
        File f=new File(getFilesDir(),qcode+".extra2");
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, apiurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String qname=response.getString("problem_name");
                    String cname="Practice Question";
                    if(!code.equals("PRACTICE")) {
                        cname=response.getString("contest_name");
                    }
                    Intent i=new Intent(getApplicationContext(),QuestionActivity.class);
                    i.putExtra("code",qcode);
                    i.putExtra("name",qname);
                    i.putExtra("contest",code);
                    i.putExtra("cname",cname);
                    startActivity(i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    StaticHelper.openBrowser("https://www.codechef.com/"+code,getApplicationContext());
                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"Error loading",Toast.LENGTH_SHORT).show();
            }
        });
        if(f.exists()) {
            Intent i=new Intent(getApplicationContext(),QuestionActivity.class);
            swipeRefreshLayout.setRefreshing(false);
            FileInputStream fileInputStream= null;
            try {
                fileInputStream = new FileInputStream(f);
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
                i.putExtra("code", qcode);
                i.putExtra("name", bufferedReader.readLine());
                i.putExtra("contest",code);
                i.putExtra("cname",bufferedReader.readLine());
                fileInputStream.close();
                bufferedReader.close();
                startActivity(i);
                finish();
            } catch (FileNotFoundException e) {
                queue.add(jsonObjectRequest);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                queue.add(jsonObjectRequest);
            }
        }
        else {
            queue.add(jsonObjectRequest);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(jsonObjectRequest);
            }
        });
    }
}
