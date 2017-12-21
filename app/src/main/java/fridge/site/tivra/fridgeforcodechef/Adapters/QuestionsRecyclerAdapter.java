package fridge.site.tivra.fridgeforcodechef.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import fridge.site.tivra.fridgeforcodechef.DataModels.Question;
import fridge.site.tivra.fridgeforcodechef.QuestionActivity;
import fridge.site.tivra.fridgeforcodechef.R;

/**
 * Created by cogito on 12/17/17.
 */

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.QuestionHolder> {
    public ArrayList<Question> questions;
    private Toast mToastToShow = null;
    public String bodyData,extraData;

    public QuestionsRecyclerAdapter(ArrayList<Question> questions) {
        this.questions=questions;
    }
    @Override
    public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card,parent,false);
        QuestionHolder qh=new QuestionHolder(v);
        return qh;
    }

    @Override
    public void onBindViewHolder(final QuestionHolder holder, final int position) {
        final Question question=questions.get(position);
        holder.questionName.setText(question.questionName);
        holder.successDetails.setText(question.successDetails);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(holder.context,QuestionActivity.class);
                i.putExtra("code",questions.get(position).questionCode);
                i.putExtra("name",questions.get(position).questionTitle);
                i.putExtra("contest",questions.get(position).contestCode);
                i.putExtra("cname",questions.get(position).contestName);
                holder.context.startActivity(i);
            }
        });
        holder.questionCode=question.questionCode;
        File f = new File(holder.context.getFilesDir(),holder.questionCode+".body1");
        if(f.exists()) {
            holder.button.setImageDrawable(holder.context.getDrawable(R.drawable.ic_delete_white_24dp));
            holder.button.setBackgroundDrawable(holder.context.getDrawable(R.drawable.round_button_delete));
        }
        else {
            holder.button.setImageDrawable(holder.context.getDrawable(R.drawable.ic_file_download_white_24dp));
            holder.button.setBackgroundDrawable(holder.context.getDrawable(R.drawable.round_button_download));
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(holder.context.getFilesDir(),holder.questionCode+".body1");
                Log.d("Hooo",holder.questionCode+".body1");
                if(f.exists()) {
                    f.delete();
                    f=new File(holder.context.getFilesDir(),holder.questionCode+".extra2");
                    f.delete();
                    showToast(holder.context,"Deleted "+question.questionTitle,1000);
                    holder.button.setImageDrawable(holder.context.getDrawable(R.drawable.ic_file_download_white_24dp));
                    holder.button.setBackgroundDrawable(holder.context.getDrawable(R.drawable.round_button_download));
                    return;
                }
                holder.button.setBackgroundDrawable(holder.context.getDrawable(R.drawable.round_button_progress));
                holder.queue= Volley.newRequestQueue(holder.context);
                String apiurl="https://www.codechef.com/api/contests/"+question.contestCode+"/problems/"+holder.questionCode;
                final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, apiurl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String body= "",time_limit="",source_limit="";
                        try {
                            body = response.getString("body");
                            time_limit=response.getString("max_timelimit");
                            source_limit=response.getString("source_sizelimit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        BufferedReader bufferedReader=new BufferedReader(new StringReader(body));
                        StringBuilder str=new StringBuilder("");
                        String temp;
                        QuestionActivity.makeBody(bufferedReader,str);
                        holder.button.setImageDrawable(holder.context.getDrawable(R.drawable.ic_delete_white_24dp));
                        holder.button.setBackgroundDrawable(holder.context.getDrawable(R.drawable.round_button_delete));

                        bodyData=str.toString();
                        extraData="Time Limit: "+time_limit+" sec  Source limit: "+source_limit+" bytes";
                        File file=new File(holder.context.getFilesDir(),holder.questionCode+".body1");
                        File file2=new File(holder.context.getFilesDir(),holder.questionCode+".extra2");
                        try {
                            FileOutputStream fileOutputStream=holder.context.openFileOutput(holder.questionCode+".body1", Context.MODE_PRIVATE);
                            fileOutputStream.write(bodyData.getBytes(Charset.forName("UTF-8")));
                            fileOutputStream.close();
                            FileOutputStream fileOutputStream1=holder.context.openFileOutput(holder.questionCode+".extra2",Context.MODE_PRIVATE);
                            fileOutputStream1.write((question.questionName+"\n"+question.contestName+"\n"+extraData).getBytes(Charset.forName("UTF-8")));
                            fileOutputStream1.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast(holder.context,"An Error Occured",1000);
                    }
                });
                holder.queue.add(jsonObjectRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void showToast(Context context,String message,int timeInMSecs) {
        if (mToastToShow != null) {
            mToastToShow.cancel();
        }
        // Set the toast and duration
        mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

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
    public void onViewRecycled(QuestionHolder holder) {
        super.onViewRecycled(holder);
        if(holder.queue!=null)
            holder.queue.cancelAll(Request.Method.GET);
        holder.queue=null;
    }

    public class QuestionHolder extends RecyclerView.ViewHolder {
        public RequestQueue queue;
        private final Context context;
        public AppCompatImageButton button;
        public String questionCode;
        public TextView questionName,successDetails;
        public QuestionHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            questionName=itemView.findViewById(R.id.question_name);
            successDetails=itemView.findViewById(R.id.success_details);
            button=itemView.findViewById(R.id.download_button);
        }
    }
}
