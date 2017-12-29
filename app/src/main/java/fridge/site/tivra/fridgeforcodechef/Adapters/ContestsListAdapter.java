package fridge.site.tivra.fridgeforcodechef.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fridge.site.tivra.fridgeforcodechef.ContestActivity;
import fridge.site.tivra.fridgeforcodechef.DataModels.Contest;
import fridge.site.tivra.fridgeforcodechef.R;

/**
 * Created by cogito on 12/17/17.
 */

public class ContestsListAdapter extends RecyclerView.Adapter<ContestsListAdapter.ContestHolder> {
    public ArrayList<Contest> contests, filtered;

    public ContestsListAdapter(ArrayList<Contest> contests) {
        this.contests = contests;
        this.filtered = contests;
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
                if (contest.flag == 1)
                    Toast.makeText(holder.context, "Contest yet to start", Toast.LENGTH_SHORT).show();
                else {
                    Intent i = new Intent(holder.context, ContestActivity.class);
                    i.putExtra("code", contest.contestCode);
                    i.putExtra("name", contest.contestName);
                    i.putExtra("start", contest.startDate);
                    i.putExtra("end", contest.endDate);
                    if (contest.flag == 2)
                        i.putExtra("practice", true);
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
