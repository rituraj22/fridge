package fridge.site.tivra.fridgeforcodechef.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import fridge.site.tivra.fridgeforcodechef.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LicenseFragment extends DialogFragment {

    String value="<h2>jsoup (https://jsoup.org)<br>" +
            "Copyright © 2009 - 2017 Jonathan Hedley (jonathan@hedley.net)</h2><br>" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:<br>" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br>" +
            "<br>" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.<br>" +
            "<br>" +
            "<br>" +
            "<h2>HtmlTextView for Android (https://github.com/PrivacyApps/html-textview)<br>" +
            "Copyright (C) 2016 Richard Thai</h2><br>" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
            "you may not use this file except in compliance with the License.<br>" +
            "You may obtain a copy of the License at<br>" +
            "<br>" +
            "    http://www.apache.org/licenses/LICENSE-2.0<br>" +
            "<br>" +
            "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.";

    public LicenseFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_license, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView t=view.findViewById(R.id.license_text);
        t.setText(Html.fromHtml(value));
    }
}
