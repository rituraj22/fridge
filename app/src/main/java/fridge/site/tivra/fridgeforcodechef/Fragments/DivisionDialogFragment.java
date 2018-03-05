package fridge.site.tivra.fridgeforcodechef.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fridge.site.tivra.fridgeforcodechef.ContestLoaderActivity;
import fridge.site.tivra.fridgeforcodechef.QuestionLoaderActivity;
import fridge.site.tivra.fridgeforcodechef.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DivisionDialogFragment extends DialogFragment {

    ArrayList<String> names,ranges,codes;
    ArrayList<LinearLayout> layouts;
    ArrayList<TextView> nameViews,rangeViews;

    public DivisionDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_division_dialog, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle temp=getArguments();
        names=temp.getStringArrayList("names");
        ranges=temp.getStringArrayList("ranges");
        codes=temp.getStringArrayList("codes");
        for(int i=0;i<3;i++) {
            if(names.size()>i) {
                nameViews.get(i).setText(names.get(i));
                rangeViews.get(i).setText("Rating: "+ranges.get(i));
                final String ccode=codes.get(i);
                layouts.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getActivity()!=null) {
                            Intent i = new Intent(getActivity(), ContestLoaderActivity.class);
                            i.putExtra("code", ccode);
                            startActivity(i);
                            getActivity().finish();
                        }
                    }
                });
            }
            else {
                layouts.get(i).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layouts=new ArrayList<>();
        nameViews=new ArrayList<>();
        rangeViews=new ArrayList<>();

        layouts.add((LinearLayout)view.findViewById(R.id.division_one_layout));
        nameViews.add((TextView)view.findViewById(R.id.division_one_name));
        rangeViews.add((TextView)view.findViewById(R.id.division_one_range));

        layouts.add((LinearLayout)view.findViewById(R.id.division_two_layout));
        nameViews.add((TextView)view.findViewById(R.id.division_two_name));
        rangeViews.add((TextView)view.findViewById(R.id.division_two_range));

        layouts.add((LinearLayout)view.findViewById(R.id.division_three_layout));
        nameViews.add((TextView)view.findViewById(R.id.division_three_name));
        rangeViews.add((TextView)view.findViewById(R.id.division_three_range));
    }
}
