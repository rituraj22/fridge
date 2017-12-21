package fridge.site.tivra.fridgeforcodechef;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by cogito on 12/20/17.
 */

public class StaticHelper {
    public static void openBrowser(String url, Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String[] browsers=new String[6];
        browsers[0]= "com.android.chrome";
        browsers[1]="org.mozilla.firefox";
        browsers[2]="com.android.browser";
        browsers[3]="com.UCMobile.intl";
        browsers[4]=null;
        browsers[5]=null;
        i.setPackage("com.brave.browser");
        for(int j=0;j<6;j++) {
            try {
                context.startActivity(i);
                j=7;
            }
            catch (ActivityNotFoundException e) {
                i.setPackage(browsers[j]);
            }
        }
    }
}
