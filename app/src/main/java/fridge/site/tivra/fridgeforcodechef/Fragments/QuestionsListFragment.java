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
import fridge.site.tivra.fridgeforcodechef.DataModels.Question;
import fridge.site.tivra.fridgeforcodechef.Adapters.QuestionsLiteRecyclerAdapter;
import fridge.site.tivra.fridgeforcodechef.R;

public class QuestionsListFragment extends Fragment {
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    QuestionsLiteRecyclerAdapter questionsRecyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Question> questions;
    android.support.v7.widget.SearchView searchView;
    String filter = "";
    View rootView;
    TextView offlineQuestionsPlaceholder;


    int positionIndex;
    int topView;

    public QuestionsListFragment() {
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
        return inflater.inflate(R.layout.fragment_questions_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.questions_recyclerview);
        questions = new ArrayList<Question>();
        rootView = getActivity().findViewById(R.id.root_questions);
        setHasOptionsMenu(true);
        offlineQuestionsPlaceholder = getActivity().findViewById(R.id.offline_questions_placeholder);
        questionsRecyclerAdapter = new QuestionsLiteRecyclerAdapter(questions);
        recyclerView.setAdapter(questionsRecyclerAdapter);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = getActivity().findViewById(R.id.offline_questions_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuestions();
            }
        });
        if (savedInstanceState == null || !savedInstanceState.containsKey("questions")) {
            swipeRefreshLayout.setRefreshing(true);
            getQuestions();
        }
    }


    public void getQuestions() {
        File folder = getActivity().getFilesDir();
        File[] files = folder.listFiles();
        questions.clear();
        for (int i = files.length - 1; i >= 0; i--) {
            if (files[i].isFile() && files[i].getName().contains(".extra2")) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(files[i]);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
                    String name = bufferedReader.readLine();
                    String contestName = bufferedReader.readLine();
                    String code = files[i].getName().replaceAll(".extra2", "");

                    Question question = new Question(name, code, "", "", "PRACTICE", contestName);
                    questions.add(question);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        questionsRecyclerAdapter = new QuestionsLiteRecyclerAdapter(questions);
        recyclerView.swapAdapter(questionsRecyclerAdapter, false);
        if (filter != null)
            questionsRecyclerAdapter.setFilter(filter);
        if (questions == null || questions.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            offlineQuestionsPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            offlineQuestionsPlaceholder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions", questions);
        rememberScrollPosition();
        outState.putInt("pos",positionIndex);
        outState.putInt("top",topView);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_action_menu, menu);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.clearFocus();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint("Enter question name or code");
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

                questionsRecyclerAdapter.setFilter(query);
                filter = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                questionsRecyclerAdapter.setFilter(newText);
                filter = newText;
                return true;
            }
        });
        searchView.clearFocus();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("questions")) {
            questions = savedInstanceState.getParcelableArrayList("questions");
            questionsRecyclerAdapter = new QuestionsLiteRecyclerAdapter(questions);
            recyclerView.swapAdapter(questionsRecyclerAdapter, true);
            questionsRecyclerAdapter.setFilter(filter);
            if(savedInstanceState.containsKey("pos")&&savedInstanceState.containsKey("top")) {
                positionIndex=savedInstanceState.getInt("pos");
                topView=savedInstanceState.getInt("top");
                setScrollPosition();
            }
        }
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
    public void onResume() {
        super.onResume();
        getQuestions();
        if (searchView != null) {
            filter = searchView.getQuery().toString();
            searchView.clearFocus();
        }
        questionsRecyclerAdapter.setFilter(filter);
        setScrollPosition();
        rootView.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        rememberScrollPosition();
    }

    public void rememberScrollPosition() {
        positionIndex= linearLayoutManager.findFirstVisibleItemPosition();
        View startView = recyclerView.getChildAt(0);
        topView = (startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());
    }

    public void setScrollPosition() {
        if (positionIndex!= -1) {
            linearLayoutManager.scrollToPositionWithOffset(positionIndex, topView);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
