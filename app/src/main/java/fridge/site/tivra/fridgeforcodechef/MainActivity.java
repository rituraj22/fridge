package fridge.site.tivra.fridgeforcodechef;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import fridge.site.tivra.fridgeforcodechef.Fragments.ContestsListFragment;
import fridge.site.tivra.fridgeforcodechef.Fragments.OfflineContestsFragment;
import fridge.site.tivra.fridgeforcodechef.Fragments.QuestionsListFragment;


public class MainActivity extends AppCompatActivity {
    MainPagerAdapter mainPagerAdapter;
    ViewPager viewPager;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
            Log.e("res", "" + resultCode);
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("Unsupported", "This device is not supported.");
            }
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
        String url = getIntent().getDataString();
        if (url != null) {
            String fullurl = url;
            int ind = url.indexOf("codechef.com");
            ind += 12;
            url = url.substring(ind);
            String[] parts = url.split("/");
            int length = parts.length;
            String contest;
            boolean status = false;
            if (length > 1) {
                contest = parts[1];
                if (contest.equals("problems")) {
                    contest = "PRACTICE";
                    String qcode = parts[2];
                    Intent i = new Intent(this, QuestionLoaderActivity.class);
                    i.putExtra("code", contest);
                    i.putExtra("qcode", qcode);
                    startActivity(i);
                    status = true;
                } else if (length == 2) {

                    Intent i = new Intent(this, ContestLoaderActivity.class);
                    i.putExtra("code", contest);
                    startActivity(i);
                    status = true;
                    //opne contest
                } else if (length == 4) {
                    String qcode = parts[3];
                    Intent i = new Intent(this, QuestionLoaderActivity.class);
                    i.putExtra("code", contest);
                    i.putExtra("qcode", qcode);
                    startActivity(i);
                    status = true;
                }
            }
            if (!status) {
                StaticHelper.openBrowser(fullurl, getApplicationContext());
                finish();
            }
        }
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.direct_code_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DirectCodeActivity.class);
                startActivity(i);
            }
        });

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mainPagerAdapter);
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class MainPagerAdapter extends FragmentStatePagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Fragment fragment = new ContestsListFragment();
                return fragment;
            } else if (position == 1) {
                Fragment fragment = new QuestionsListFragment();
                return fragment;
            } else {
                Fragment fragment = new OfflineContestsFragment();
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Online Contests";
            else if (position == 1)
                return "Saved Questions";
            else
                return "Saved Contests";
        }
    }
}
