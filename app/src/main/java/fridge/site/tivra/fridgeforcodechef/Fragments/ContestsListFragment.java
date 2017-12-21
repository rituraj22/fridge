package fridge.site.tivra.fridgeforcodechef.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.psdev.licensesdialog.LicensesDialog;
import fridge.site.tivra.fridgeforcodechef.AboutActivity;
import fridge.site.tivra.fridgeforcodechef.DataModels.Contest;
import fridge.site.tivra.fridgeforcodechef.Adapters.ContestsListAdapter;
import fridge.site.tivra.fridgeforcodechef.DataModels.Question;
import fridge.site.tivra.fridgeforcodechef.MainActivity;
import fridge.site.tivra.fridgeforcodechef.R;

public class ContestsListFragment extends Fragment {

    android.support.v7.widget.SearchView searchView;
    String filter;

    public ContestsListFragment() {
        // Required empty public constructor
    }

    HtmlTextView textView;
    SwipeRefreshLayout swipeRefreshLayout;
    StringRequest stringRequest;
    RequestQueue queue;
    TextView contestsPlaceholder;
    RecyclerView recyclerView;
    ContestsListAdapter contestsListAdapter;
    ArrayList<Contest> contestArrayList;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contests, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        rootView=getActivity().findViewById(R.id.root_contests);
        contestArrayList = new ArrayList<Contest>();
        contestsPlaceholder = getActivity().findViewById(R.id.online_contests_placeholder);
        recyclerView = getActivity().findViewById(R.id.contests_recyclerview);
        contestsListAdapter = new ContestsListAdapter(contestArrayList);
        recyclerView.setAdapter(contestsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = getActivity().findViewById(R.id.refresh_layout);
        queue = Volley.newRequestQueue(getContext());
        String url = "https://www.codechef.com/contests";
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseContests(response);
                Log.d("Hoo", response);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Hoo", error.toString());
                if (isResumed()) {
                    Toast.makeText(getActivity(), "Error loading", Toast.LENGTH_SHORT);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        if (savedInstanceState == null) {
            queue.add(stringRequest);
            swipeRefreshLayout.setRefreshing(true);
        }
        if(searchView!=null)
            searchView.clearFocus();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(stringRequest);
            }
        });
    }

    public void parseContests(String html) {
        Document doc = Jsoup.parse(html);
        Elements h3 = doc.select("h3");
        Elements tables = doc.select(".dataTable");
        int flag = 0;
        contestArrayList.clear();
        for (Element table : tables) {
            Elements tbody = table.getElementsByTag("tbody");
            Elements contests = tbody.get(0).children();
            for (Element contest : contests) {
                Element code = contest.child(0);
                Element name = contest.child(1);
                Element startDate = contest.child(2);
                Element endDate = contest.child(3);
                String cCode = code.text();
                String cName = name.text();
                String sDate = startDate.text();
                String eDate = endDate.text();
                Contest contest1 = new Contest(cCode, cName, sDate, eDate, flag);
                contestArrayList.add(contest1);
            }
            flag++;
        }
        contestsListAdapter = new ContestsListAdapter(contestArrayList);
        recyclerView.swapAdapter(contestsListAdapter, false);
        if (filter != null)
            contestsListAdapter.setFilter(filter);

        if (contestArrayList == null || contestArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            contestsPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            contestsPlaceholder.setVisibility(View.GONE);
        }
        Log.d("Hoo", "hi    " + contestArrayList.size());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (contestArrayList == null || contestArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            contestsPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            contestsPlaceholder.setVisibility(View.GONE);
            contestsListAdapter = new ContestsListAdapter(contestArrayList);
            recyclerView.swapAdapter(contestsListAdapter, true);
        }
        if (searchView != null) {
            filter = searchView.getQuery().toString();
            searchView.clearFocus();
        }
        contestsListAdapter.setFilter(filter);
        rootView.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("contests", contestArrayList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("contests")) {
            contestArrayList = savedInstanceState.getParcelableArrayList("contests");
            contestsListAdapter = new ContestsListAdapter(contestArrayList);
            recyclerView.swapAdapter(contestsListAdapter, true);
            contestsListAdapter.setFilter(filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_action_menu, menu);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_button:
                Intent i = new Intent(getContext(), AboutActivity.class);
                getActivity().startActivity(i);
                break;
            case R.id.license_button:
                new LicensesDialog.Builder(getActivity())
                        .setNotices(R.raw.notices)
                        .build()
                        .show();
                break;

            case R.id.share_menu_main:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                startActivity(Intent.createChooser(sharingIntent, "Share app via"));
                break;
        }
        return true;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint("Enter contest name or code");
        searchView.setQuery(filter, true);
        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        if (v != null)
            v.setBackgroundColor(Color.TRANSPARENT);


        android.support.v7.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setHintTextColor(Color.LTGRAY);

        searchView.setBackground(getResources().getDrawable(R.drawable.search_bar_bg));


        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                contestsListAdapter.setFilter(query);
                filter = query;
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contestsListAdapter.setFilter(newText);
                filter = newText;
                return true;
            }
        });
        searchView.clearFocus();
    }
}
