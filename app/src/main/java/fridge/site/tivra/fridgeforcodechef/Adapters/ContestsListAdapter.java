package fridge.site.tivra.fridgeforcodechef.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fridge.site.tivra.fridgeforcodechef.ContestActivity;
import fridge.site.tivra.fridgeforcodechef.DataModels.Contest;
import fridge.site.tivra.fridgeforcodechef.R;
import fridge.site.tivra.fridgeforcodechef.StaticHelper;

/**
 * Created by cogito on 12/17/17.
 */

public class ContestsListAdapter extends RecyclerView.Adapter<ContestsListAdapter.ContestHolder> {
    public ArrayList<Contest> contests, filtered;
    private Context context;

    public ContestsListAdapter(ArrayList<Contest> contests,Context context) {
        this.contests = contests;
        this.filtered = contests;
        this.context =  context;
    }

    @Override
    public ContestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_card, parent, false);
        ContestHolder c = new ContestHolder(v);
        return c;
    }

    @Override
    public void onBindViewHolder(final ContestHolder holder, int position) {
        final Contest contest = filtered.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contest.flag == 1) {
                    Toast.makeText(holder.context, "Contest yet to start", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    try {
                        final Date startDate=simpleDateFormat.parse(contest.startDate);
                        final Date endDate=simpleDateFormat.parse(contest.endDate);
                        new AlertDialog.Builder(context)
                                .setTitle("Add to calendar")
                                .setMessage("Do you want to add "+contest.contestName+" to your calendar")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_INSERT);
                                        intent.setType("vnd.android.cursor.item/event");
                                        Calendar cal = Calendar.getInstance();
                                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime());
                                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endDate.getTime());
                                        intent.putExtra(CalendarContract.Events.TITLE, contest.contestName);
                                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,  "http://www.codechef.com/"+contest.contestCode);
                                        intent.putExtra(CalendarContract.Events.DESCRIPTION,"The link for the contest is: http://www.codechef.com/"+contest.contestCode+" .Set via fridge");
                                        context.startActivity(intent);
                                    }
                                }).setNegativeButton("No", null).show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Intent i = new Intent(holder.context, ContestActivity.class);
                    i.putExtra("code", contest.contestCode);
                    i.putExtra("name", contest.contestName);
                    i.putExtra("start", contest.startDate);
                    i.putExtra("end", contest.endDate);
                    holder.context.startActivity(i);
                }
            }
        });
        switch (contest.flag) {
            case -1:
                holder.imageView.setVisibility(View.GONE);
                break;
            case 0:
                holder.imageView.setImageResource(R.drawable.ic_now_24dp);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.ic_future_24dp);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.ic_past_24dp);
                break;
        }
        holder.contestName.setText(contest.contestName);
        holder.startDate.setText(contest.startDate);
        holder.endDate.setText(contest.endDate);
    }

    @Override
    public int getItemCount() {
        if (filtered != null)
            return filtered.size();
        else
            return 0;
    }

    public class ContestHolder extends RecyclerView.ViewHolder {
        public TextView contestName, startDate, endDate;
        public AppCompatImageView imageView;
        private Context context;

        public ContestHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.contest_status);
            contestName = itemView.findViewById(R.id.contest_name);
            startDate = itemView.findViewById(R.id.start_date);
            endDate = itemView.findViewById(R.id.end_date);
        }
    }

    public void setFilter(String str) {
        if (str == null)
            return;
        str = str.toLowerCase();
        ArrayList<Contest> temp = new ArrayList<Contest>();
        for (Contest c : contests) {
            if (c.contestName.toLowerCase().equals(str) || c.contestCode.toLowerCase().equals(str)) {
                temp.add(c);
            }
        }
        for (Contest c : contests) {
            if (c.contestName.toLowerCase().contains(str) || c.contestCode.toLowerCase().contains(str)) {
                if (!(c.contestName.toLowerCase().equals(str) || c.contestCode.toLowerCase().equals(str))) {
                    temp.add(c);
                }
            }
        }
        filtered = temp;
        notifyDataSetChanged();
    }
}
