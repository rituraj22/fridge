package fridge.site.tivra.fridgeforcodechef.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.io.File;
import java.util.ArrayList;

import fridge.site.tivra.fridgeforcodechef.DataModels.Question;
import fridge.site.tivra.fridgeforcodechef.QuestionActivity;
import fridge.site.tivra.fridgeforcodechef.R;

/**
 * Created by cogito on 12/17/17.
 */

public class QuestionsLiteRecyclerAdapter extends RecyclerView.Adapter<QuestionsLiteRecyclerAdapter.QuestionHolder> {
    public ArrayList<Question> questions,filtered;
    private Toast mToastToShow = null;
    public String bodyData, extraData;

    public QuestionsLiteRecyclerAdapter(ArrayList<Question> questions) {
        this.questions = questions;
        this.filtered=questions;
    }

    @Override
    public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);
        QuestionHolder qh = new QuestionHolder(v);
        return qh;
    }

    @Override
    public void onBindViewHolder(final QuestionHolder holder, final int position) {
        final Question question = filtered.get(position);
        holder.questionName.setText(question.questionTitle);
        holder.successDetails.setText(question.contestName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.context, QuestionActivity.class);
                i.putExtra("code", filtered.get(position).questionCode);
                i.putExtra("name", filtered.get(position).questionTitle);
                i.putExtra("contest", filtered.get(position).contestCode);
                i.putExtra("cname", filtered.get(position).contestName);
                holder.context.startActivity(i);
            }
        });
        holder.questionCode = question.questionCode;
        File f = new File(holder.context.getFilesDir(), holder.questionCode + ".body1");
        if (f.exists()) {
            holder.button.setImageResource(R.drawable.ic_delete_white_24dp);
            holder.button.setBackgroundDrawable(holder.context.getResources().getDrawable(R.drawable.round_button_delete));
        } else {
            holder.button.setImageResource(R.drawable.ic_file_download_white_24dp);
            holder.button.setBackgroundDrawable(holder.context.getResources().getDrawable(R.drawable.round_button_download));
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(holder.context.getFilesDir(), holder.questionCode + ".body1");
                if (f.exists()) {
                    f.delete();
                    f = new File(holder.context.getFilesDir(), holder.questionCode + ".extra2");
                    f.delete();
                    showToast(holder.context, "Deleted " + question.questionTitle, 1000);
                    filtered.remove(question);
                    notifyDataSetChanged();
                    return;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public void showToast(Context context, String message, int timeInMSecs) {
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
        if (holder.queue != null)
            holder.queue.cancelAll(Request.Method.GET);
        holder.queue = null;
    }

    public class QuestionHolder extends RecyclerView.ViewHolder {
        public RequestQueue queue;
        private final Context context;
        public AppCompatImageButton button;
        public String questionCode;
        public TextView questionName, successDetails;

        public QuestionHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            questionName = itemView.findViewById(R.id.question_name);
            successDetails = itemView.findViewById(R.id.success_details);
            button = itemView.findViewById(R.id.download_button);
        }
    }
    public void setFilter(String str) {
        if(str==null)
            return;
        str=str.toLowerCase();
        ArrayList<Question> temp=new ArrayList<Question>();
        for(Question c:questions) {
            if(c.questionName.toLowerCase().equals(str)||c.questionCode.toLowerCase().equals(str)||c.contestName.toLowerCase().equals(str)||c.contestCode.toLowerCase().equals(str)) {
                temp.add(c);
            }
        }
        for(Question c:questions) {
            if(c.questionName.toLowerCase().contains(str)||c.questionCode.toLowerCase().contains(str)||c.contestName.toLowerCase().contains(str)||c.contestCode.toLowerCase().contains(str)) {
                if(!(c.questionName.toLowerCase().equals(str)||c.questionCode.toLowerCase().equals(str)||c.contestName.toLowerCase().equals(str)||c.contestCode.toLowerCase().equals(str))) {
                    temp.add(c);
                }
            }
        }
        filtered=temp;
        notifyDataSetChanged();
    }
}
