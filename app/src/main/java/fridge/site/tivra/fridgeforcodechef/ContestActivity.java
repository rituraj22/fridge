package fridge.site.tivra.fridgeforcodechef;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import fridge.site.tivra.fridgeforcodechef.Adapters.QuestionsRecyclerAdapter;
import fridge.site.tivra.fridgeforcodechef.DataModels.Question;

public class ContestActivity extends AppCompatActivity {
    String code,url,apiurl,name,startDate,endDate;
    ArrayList<Question> questions;
    RequestQueue queue;
    SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    RecyclerView recyclerView;
    private boolean downAll;
    TextView contestTimeToolbar;
    QuestionsRecyclerAdapter recyclerAdapter;
    private Toast mToastToShow = null;
    String sentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        fab=findViewById(R.id.full_contest_download);
        recyclerView=findViewById(R.id.contest_recyclerview);
        contestTimeToolbar=findViewById(R.id.contest_time_toolbar);
        swipeRefreshLayout=findViewById(R.id.contest_swipe_refresh_layout);
        Toolbar toolbar=findViewById(R.id.contest_toolbar);


        code=getIntent().getExtras().getString("code");
        name=getIntent().getExtras().getString("name");
        startDate=getIntent().getExtras().getString("start");
        endDate=getIntent().getExtras().getString("end");

        //If contest is open in practice mode the question is loaded with contest set to PRACTICE instead of the original contest
        if(getIntent().getExtras().containsKey("practice"))
            sentCode="PRACTICE";
        else
            sentCode=code;

        if(!startDate.trim().equals("")) {
            contestTimeToolbar.setText(startDate+"  -  "+endDate);
            contestTimeToolbar.setVisibility(View.VISIBLE);
        }

        fab.hide();

        url="https://www.codechef.com/"+code;
        apiurl="https://www.codechef.com/api/contests/"+code;

        questions=new ArrayList<Question>();
        recyclerAdapter=new QuestionsRecyclerAdapter(questions);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name+" ("+code+") ");

        queue= Volley.newRequestQueue(this);

        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, apiurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject problems=response.getJSONObject("problems");
                    Iterator<?> keys=problems.keys();
                    questions.clear();
                    while (keys.hasNext()) {
                        String key= (String) keys.next();
                        JSONObject temp=new JSONObject(problems.get(key).toString());
                        String qcode=temp.getString("code");
                        String qname=temp.getString("name");
                        String successful=temp.getString("successful_submissions");
                        String accuracy=temp.getString("accuracy");
                        questions.add(new Question(qname,qcode,successful,accuracy,sentCode,name));
                    }
                    //Order questions depending upon number of submissions
                    Collections.sort(questions,Collections.<Question>reverseOrder());
                    recyclerAdapter.notifyDataSetChanged();
                    updateFab(true);

                    //With all the details loaded, now contest can be downloaded
                    fab.show();
                } catch (JSONException e) {
                    showToast("Contest might be unavailable",1500);
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("An Error Occured",800);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if(savedInstanceState==null||!savedInstanceState.containsKey("questions")) {
            try {
                File file = new File(getFilesDir(), code + ".contest");

                //Check if contets details are saved, if not exception is thrown, which adds loading request to queue
                if (!file.exists())
                    throw new Exception("hoo");

                FileInputStream fileInputStream=new FileInputStream(file);
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream,Charset.forName("UTF-8")));

                name=bufferedReader.readLine();
                startDate=bufferedReader.readLine();
                endDate=bufferedReader.readLine();

                String line;
                while((line=bufferedReader.readLine())!=null) {
                    String qname,qcode,submissions,percent;
                    qname=line;
                    qcode=bufferedReader.readLine();
                    submissions=bufferedReader.readLine();
                    percent=bufferedReader.readLine();
                    Question question=new Question(qname,qcode,submissions,percent,sentCode,name);
                    questions.add(question);
                }
                recyclerAdapter.notifyDataSetChanged();
                updateFab(false);
                fileInputStream.close();
                bufferedReader.close();
            }
            catch (Exception e) {
                swipeRefreshLayout.setRefreshing(true);
                queue.add(jsonObjectRequest);
            }
        }

        //Add loading request on swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(jsonObjectRequest);
            }
        });

        //Hide fab on scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0)
                    fab.hide();
                else
                    fab.show();
            }
        });

        //Download or delete depending upon state
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downAll) {
                    //Delete all the questions data
                    for(Question q:questions) {
                        File f1=new File(getApplicationContext().getFilesDir(),q.questionCode+".body1");
                        File f2=new File(getApplicationContext().getFilesDir(),q.questionCode+".extra2");
                        f1.delete();
                        f2.delete();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                    //Delete contest data
                    File f=new File(getApplicationContext().getFilesDir(),code+".contest");
                    f.delete();
                    downAll=false;
                    updateFab(false);
                    showToast("All deleted",800);
                }
                else {
                    FileOutputStream fileOutputStream= null;
                    showToast("Preparing Download",800);
                    File file=new File(getApplicationContext().getFilesDir(),code+".contest");

                    //Write contest details to file
                    try {
                        fileOutputStream = openFileOutput(code+".contest", Context.MODE_PRIVATE);
                        StringBuilder temp=new StringBuilder();
                        temp.append(name+"\n");
                        temp.append(startDate+"\n");
                        temp.append(endDate+"\n");
                        for(Question q:questions) {
                            temp.append(q.questionTitle+"\n");
                            temp.append(q.questionCode+"\n");
                            temp.append(q.numSub+"\n");
                            temp.append(q.percent+"\n");                }
                        fileOutputStream.write(temp.toString().getBytes(Charset.forName("UTF-8")));
                        fileOutputStream.close();
                        updateFab(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Write all contests to its respective file
                    for(Question q:questions) {
                        downloadQuestion(q);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queue!=null)
            queue.cancelAll(Request.Method.GET);
    }

    //Add a Request to download and save a question, and refresh question data in recyclerview and fab
    public void downloadQuestion(final Question q) {
        File f1=new File(getApplicationContext().getFilesDir(),q.questionCode+".body1");
        if(f1.exists())
            return;
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, "https://www.codechef.com/api/contests/"+code+"/problems/"+q.questionCode, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String body= "",time_limit="",source_limit="",editorial="";
                try {
                    body = response.getString("body");
                    time_limit=response.getString("max_timelimit");
                    source_limit=response.getString("source_sizelimit");

                    //Editorial is not essential
                    try {
                        editorial=response.getString("editorial_url");
                        editorial="\nEditorial: "+editorial;
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                BufferedReader bufferedReader=new BufferedReader(new StringReader(body));
                StringBuilder str=new StringBuilder("");
                String temp;
                try {
                    int state=0;
                    while ((temp=bufferedReader.readLine())!=null) {
                        for(int i=0;i<temp.length();i++) {
                            if(temp.charAt(i)=='<')
                                state++;
                            if(temp.charAt(i)=='>') {
                                state--;
                            }
                        }
                        str.append(temp);
                        if(state==0)
                            str.append("<br>");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String bodyData=str.toString();
                String extraData="Time Limit: "+time_limit+" sec  Source limit: "+source_limit+" bytes"+editorial;

                File file=new File(getFilesDir(),q.questionCode+".body1");
                File file2=new File(getFilesDir(),q.questionCode+".extra2");
                try {
                    FileOutputStream fileOutputStream=openFileOutput(q.questionCode+".body1", Context.MODE_PRIVATE);
                    fileOutputStream.write(bodyData.getBytes(Charset.forName("UTF-8")));
                    fileOutputStream.close();
                    FileOutputStream fileOutputStream1=openFileOutput(q.questionCode+".extra2",Context.MODE_PRIVATE);
                    fileOutputStream1.write((q.questionName+"\n"+name+"\n"+extraData).getBytes(Charset.forName("UTF-8")));
                    fileOutputStream1.close();
                    updateFab(true);
                    recyclerAdapter.notifyDataSetChanged();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("Error in downloading "+q.questionTitle,1200);
            }
        });

        //add request to download question in queue
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String appName=getString(R.string.app_name);
                String appUrl=getString(R.string.app_url);
                String shareText="Visit "+url+" to participate in "+name+" \n\nSent using "+appName+" ("+appUrl+")";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Shared Codechef contest");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                startActivity(Intent.createChooser(sharingIntent,"Share contest via"));
                break;
            case R.id.action_network:
                StaticHelper.openBrowser(url,getApplicationContext());
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.share_browser,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.notifyDataSetChanged();
        updateFab(false);
    }

    public void updateFab(boolean toast) {
        File f1=new File(getApplicationContext().getFilesDir(),code+".contest");
        boolean prev=downAll;
        if(f1.exists())
            downAll=true;
        else
            downAll=false;
        for(Question q:questions) {
            File f=new File(getApplicationContext().getFilesDir(),q.questionCode+".body1");
            if(!f.exists()) {
                downAll=false;
                break;
            }
        }
        if(downAll) {
            fab.setImageResource(R.drawable.ic_delete_white_24dp);
            if(Build.VERSION.SDK_INT>=23)
                fab.setBackgroundTintList(getColorStateList(R.color.delete_color));
            else
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(211,47,47)));
        }
        else {
            fab.setImageResource(R.drawable.ic_file_download_white_24dp);
            if(Build.VERSION.SDK_INT>=23)
                fab.setBackgroundTintList(getColorStateList(R.color.download_color));
            else
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(76,191,80)));

        }
        if(toast&&!prev&&downAll) {
            showToast(name+" downloaded",1000);
        }
    }

    public void showToast(String message,int timeInMSecs) {
        if (mToastToShow != null) {
            mToastToShow.cancel();
        }
        // Set the toast and duration
        mToastToShow = Toast.makeText(this, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(timeInMSecs, timeInMSecs /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                if (mToastToShow != null) {
                    mToastToShow.show();
                }
            }

            public void onFinish() {
                if (mToastToShow != null) {
                    mToastToShow.cancel();
                }
                // Making the Toast null again
                mToastToShow = null;
                // Emptying the message to compare if its the same message being displayed or not
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions",questions);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("questions")) {
            questions=savedInstanceState.getParcelableArrayList("questions");
            recyclerAdapter=new QuestionsRecyclerAdapter(questions);
            recyclerView.swapAdapter(recyclerAdapter,true);
        }
    }
}
