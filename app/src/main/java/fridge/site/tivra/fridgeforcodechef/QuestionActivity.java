package fridge.site.tivra.fridgeforcodechef;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;

public class QuestionActivity extends AppCompatActivity {
    public String code, name, contest;
    private HtmlTextView bodyView;
    private TextView textView;
    private RequestQueue queue;
    private FloatingActionButton fab;
    private String url, apiurl;
    String bodyData = null;
    String extraData = null;
    String cname = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toast mToastToShow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        bodyView = findViewById(R.id.question_body);
        swipeRefreshLayout = findViewById(R.id.question_refresh_layout);
        textView = findViewById(R.id.question_extra);
        code = getIntent().getExtras().getString("code");
        name = getIntent().getExtras().getString("name");
        if (getIntent().getExtras().containsKey("cname"))
            cname = getIntent().getExtras().getString("cname");
        if (getIntent().getExtras().containsKey("contest"))
            contest = getIntent().getExtras().getString("contest");
        else
            contest = "PRACTICE";
        setSupportActionBar((Toolbar) findViewById(R.id.question_toolbar));
        getSupportActionBar().setTitle(name + " (" + code + ")");
        fab = findViewById(R.id.question_download);
        fab.bringToFront();
        NestedScrollView scrollView = findViewById(R.id.question_scroll_view);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        queue = Volley.newRequestQueue(this);
        url = "https://www.codechef.com/" + contest + "/problems/" + code;
        apiurl = "https://www.codechef.com/api/contests/" + contest + "/problems/" + code;
        textView.setText(url);
        if (android.os.Build.VERSION.SDK_INT >= 23)
            fab.setBackgroundTintList(getColorStateList(R.color.download_color));
        else
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(76, 191, 80)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bodyData != null) {
                    try {
                        File f = new File(getApplicationContext().getFilesDir(), code + ".body1");
                        if (f.exists()) {
                            f.delete();
                            f = new File(getApplicationContext().getFilesDir(), code + ".extra2");
                            f.delete();
                            showToast("Deleted " + name, 1000);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                fab.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_file_download_white_24dp));
                            } else {
                                fab.setImageResource(R.drawable.ic_file_download_white_24dp);
                            }
                            if (Build.VERSION.SDK_INT >= 23)
                                fab.setBackgroundTintList(getColorStateList(R.color.download_color));
                            else
                                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(76, 191, 80)));
                            return;
                        }
                        FileOutputStream fileOutputStream = openFileOutput(code + ".body1", Context.MODE_PRIVATE);
                        fileOutputStream.write(bodyData.getBytes(Charset.forName("UTF-8")));
                        fileOutputStream.close();
                        FileOutputStream fileOutputStream1 = openFileOutput(code + ".extra2", Context.MODE_PRIVATE);
                        fileOutputStream1.write((name + " (" + code + ")" + "\n" + cname + "\n" + extraData).getBytes(Charset.forName("UTF-8")));
                        fileOutputStream1.close();
                        showToast("Downloaded " + name, 1000);
                        fab.setImageResource(R.drawable.ic_delete_white_24dp);
                        if (Build.VERSION.SDK_INT >= 23)
                            fab.setBackgroundTintList(getColorStateList(R.color.delete_color));
                        else
                            fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(211, 47, 47)));
                    } catch (Exception e) {
                        showToast("Error", 1000);
                    }
                } else {
                    showToast("Error", 1000);
                }
            }
        });
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String body = "", time_limit = "", source_limit = "", editorial = "";
                try {
                    body = response.getString("body");
                    time_limit = response.getString("max_timelimit");
                    source_limit = response.getString("source_sizelimit");
                    try {
                        editorial = response.getString("editorial_url");
                        editorial = "\nEditorial: " + editorial;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Question might be unavailable", 1500);
                    swipeRefreshLayout.setRefreshing(false);
                }
                BufferedReader bufferedReader = new BufferedReader(new StringReader(body));
                StringBuilder str = new StringBuilder("");
                String temp;
                makeBody(bufferedReader, str);
                bodyData = str.toString();
                bodyView.setHtml(bodyData, new HtmlHttpImageGetter(bodyView));
                extraData = "Time Limit: " + time_limit + " sec  Source limit: " + source_limit + " bytes" + editorial;
                textView.setText(extraData);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("An error occured", 1000);
                bodyView.setText("An error occured");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(jsonObjectRequest);
            }
        });
        if (savedInstanceState == null || !savedInstanceState.containsKey("body")) {
            try {
                File file = new File(getFilesDir(), code + ".body1");
                if (!file.exists())
                    throw new Exception("hoo");
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
                StringBuilder temp = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    temp.append(line);
                    temp.append('\n');
                }
                fileInputStream.close();
                bufferedReader.close();
                bodyData = temp.toString();
                File file2 = new File(getFilesDir(), code + ".extra2");
                FileInputStream fileInputStream2 = new FileInputStream(file2);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream2, Charset.forName("UTF-8")));
                bufferedReader.readLine();
                bufferedReader.readLine();
                extraData = bufferedReader.readLine();
                if ((line = bufferedReader.readLine()) != null)
                    extraData = extraData + ("\n" + line);
                fileInputStream2.close();
                bodyView.setHtml(bodyData, new HtmlHttpImageGetter(bodyView));
                textView.setText(extraData);
                fab.setImageResource(R.drawable.ic_delete_white_24dp);
                if (android.os.Build.VERSION.SDK_INT >= 23)
                    fab.setBackgroundTintList(getColorStateList(R.color.delete_color));
                else
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(211, 47, 47)));
            } catch (Exception e) {
                swipeRefreshLayout.setRefreshing(true);
                queue.add(jsonObjectRequest);
            }

        }

    }

    public static void makeBody(BufferedReader bufferedReader, StringBuilder str) {
        String temp;
        try {
            int state = 0;
            while ((temp = bufferedReader.readLine()) != null) {
                for (int i = 0; i < temp.length(); i++) {
                    if (temp.charAt(i) == '<' && temp.length() != i + 1 && temp.charAt(i + 1) != ' ')
                        state++;
                    if (temp.charAt(i) == '>' && i != 0 && temp.charAt(i - 1) != ' ') {
                        state--;
                    }
                }
                str.append(temp);
                if (state != 0) {
                    //hello
                }
                if (state == 0 && !temp.trim().equals(""))
                    str.append("<br>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bodyData != null) {
            outState.putString("body", bodyData);
            outState.putString("extra", extraData);
            queue.stop();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null)
            queue.cancelAll(Request.Method.GET);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String appName = getString(R.string.app_name);
                String appUrl = getString(R.string.app_url);
                String shareText = "Check out this question : " + name + " at " + url + " \n\nSent using " + appName + " (" + appUrl + ")";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Codechef question ");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(sharingIntent, "Share question via"));
                break;
            case R.id.action_network:
                StaticHelper.openBrowser(url, getApplicationContext());
                break;
        }
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("body")) {
            bodyData = savedInstanceState.getString("body");
            extraData = savedInstanceState.getString("extra");
            bodyView.setHtml(bodyData, new HtmlHttpImageGetter(bodyView));
            textView.setText(extraData);
            File f = new File(getApplicationContext().getFilesDir(), code + ".body1");
            if (f.exists()) {
                fab.setImageResource(R.drawable.ic_delete_white_24dp);
                if (android.os.Build.VERSION.SDK_INT >= 23)
                    fab.setBackgroundTintList(getColorStateList(R.color.delete_color));
                else
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(211, 47, 47)));
            }
        }
    }

    public void showToast(String message, int timeInMSecs) {
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

}
