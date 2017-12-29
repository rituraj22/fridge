package fridge.site.tivra.fridgeforcodechef;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DirectCodeActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_code);
        editText=findViewById(R.id.direct_code_text);
        setSupportActionBar((Toolbar) findViewById(R.id.direct_code_toolbar));
        getSupportActionBar().setTitle("Enter question or contest code/url");
        radioGroup=findViewById(R.id.code_radio_group);
        button=findViewById(R.id.direct_code_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=editText.getText().toString();
                str=str.toUpperCase().trim();
                Intent i;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.question_radio:
                        i=new Intent(getApplicationContext(),QuestionLoaderActivity.class);
                        i.putExtra("code","PRACTICE");
                        i.putExtra("qcode",str);
                        startActivity(i);
                        break;
                    case R.id.contest_radio:
                        i=new Intent(getApplicationContext(),ContestLoaderActivity.class);
                        i.putExtra("code",str);
                        startActivity(i);
                        break;
                    case R.id.url_radio:
                        String url=editText.getText().toString().trim();
                        String fullurl = url;
                        if(!url.contains("codechef.com")) {
                            Toast.makeText(getApplicationContext(),"Invalid url",Toast.LENGTH_SHORT).show();
                            break;
                        }
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
                                i = new Intent(getApplicationContext(), QuestionLoaderActivity.class);
                                i.putExtra("code", contest);
                                i.putExtra("qcode", qcode);
                                startActivity(i);
                                status = true;
                            } else if (length == 2) {

                                i = new Intent(getApplicationContext(), ContestLoaderActivity.class);
                                i.putExtra("code", contest);
                                startActivity(i);
                                status = true;
                                //opne contest
                            } else if (length == 4) {
                                String qcode = parts[3];
                                i = new Intent(getApplicationContext(), QuestionLoaderActivity.class);
                                i.putExtra("code", contest);
                                i.putExtra("qcode", qcode);
                                startActivity(i);
                                status = true;
                            }
                        }
                        if (!status) {
                            StaticHelper.openBrowser(fullurl, getApplicationContext());
                        }
                }

            }
        });

    }

}
