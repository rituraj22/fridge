package fridge.site.tivra.fridgeforcodechef;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    ImageView github,play,mail;
    TextView versionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.about_toolbar));
        getSupportActionBar().setTitle("About Fridge for Codechef");
        String versionName = "";
        versionView=findViewById(R.id.app_version);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionView.setText("Version : "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionView.setText(getString(R.string.app_name));
        }

        github=findViewById(R.id.github_button);
        play=findViewById(R.id.play_button);
        mail=findViewById(R.id.mail_button);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.github.com/rituraj22/fridge";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://play.google.com/store/apps/details?id=fridge.site.tivra.fridgeforcodechef";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO change email
                Intent i=new Intent(Intent.ACTION_SENDTO);
                String [] adresses=new String[1];
                adresses[0]="rituraj22in@gmail.com";
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL,adresses);
                i.putExtra(Intent.EXTRA_SUBJECT,"Feedback regarding Fridge for Codechef");
                if(i.resolveActivity(getPackageManager())!=null)
                    startActivity(i);

            }
        });
//
//        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(60,60);
//        github.setLayoutParams(params);
//        play.setLayoutParams(params);
//        mail.setLayoutParams(params);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.only_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        startActivity(Intent.createChooser(sharingIntent, "Share app via"));
        return super.onOptionsItemSelected(item);
    }
}
