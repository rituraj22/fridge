package fridge.site.tivra.fridgeforcodechef.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import de.psdev.licensesdialog.LicensesDialog;
import fridge.site.tivra.fridgeforcodechef.AboutActivity;
import fridge.site.tivra.fridgeforcodechef.DataModels.Contest;
import fridge.site.tivra.fridgeforcodechef.Adapters.ContestsListAdapter;
import fridge.site.tivra.fridgeforcodechef.R;


public class OfflineContestsFragment extends Fragment {
    RecyclerView recyclerView;
    ContestsListAdapter contestsListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Contest> contests;
    TextView offlineContestsPlaceholder;
    android.support.v7.widget.SearchView searchView;
    String filter;
    View rootView;

    public OfflineContestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline_contests_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        rootView = getActivity().findViewById(R.id.root_offline_contsets);
        recyclerView = getActivity().findViewById(R.id.offline_contests_recycler_view);
        contests = new ArrayList<Contest>();
        offlineContestsPlaceholder = getActivity().findViewById(R.id.offline_contests_placeholder);
        contestsListAdapter = new ContestsListAdapter(contests);
        recyclerView.setAdapter(contestsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = getActivity().findViewById(R.id.offline_contests_swipe_refresh_layout);
        if (savedInstanceState == null || !savedInstanceState.containsKey("contests")) {
            swipeRefreshLayout.setRefreshing(true);
            getContests();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContests();
            }
        });
    }

    public void getContests() {
        File folder = getActivity().getFilesDir();
        File[] files = folder.listFiles();
        contests.clear();
        for (int i = files.length - 1; i >= 0; i--) {
            if (files[i].isFile() && files[i].getName().contains(".contest")) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(files[i]);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
                    String name = bufferedReader.readLine();
                    String startDate = bufferedReader.readLine();
                    String endDate = bufferedReader.readLine();
                    String code = files[i].getName().replaceAll(".contest", "");
                    Contest contest = new Contest(code, name, startDate, endDate, -1);
                    contests.add(contest);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("vsauce", files[i].getName() + "    " + files[i].lastModified());
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        contestsListAdapter = new ContestsListAdapter(contests);
        recyclerView.swapAdapter(contestsListAdapter, true);
        if (filter != null)
            contestsListAdapter.setFilter(filter);

        if (contests == null || contests.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            offlineContestsPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            offlineContestsPlaceholder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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


        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setHintTextColor(Color.LTGRAY);

        searchView.setBackground(getResources().getDrawable(R.drawable.search_bar_bg));

        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter = query;
                contestsListAdapter.setFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter = newText;
                contestsListAdapter.setFilter(newText);
                return true;
            }
        });
        searchView.clearFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("contests", contests);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("contests")) {
            contests = savedInstanceState.getParcelableArrayList("contests");
            contestsListAdapter = new ContestsListAdapter(contests);
            recyclerView.swapAdapter(contestsListAdapter, false);
            contestsListAdapter.setFilter(filter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContests();
        rootView.requestFocus();
        if (searchView != null) {
            filter = searchView.getQuery().toString();
            searchView.clearFocus();
        }
        contestsListAdapter.setFilter(filter);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
