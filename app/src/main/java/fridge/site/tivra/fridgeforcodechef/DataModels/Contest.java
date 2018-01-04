package fridge.site.tivra.fridgeforcodechef.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cogito on 12/17/17.
 */

public class Contest implements Parcelable {
    public String contestName, startDate, endDate, contestCode;
    public int flag;
    private ArrayList<Question> questions;

    public Contest(String contestCode, String contestName, String startDate, String endDate, int flag) {
        this.contestCode = contestCode;
        this.contestName = contestName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questions = questions;
        this.flag = flag;
    }

    protected Contest(Parcel in) {
        contestName = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        contestCode = in.readString();
        flag = in.readInt();
        questions = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Contest> CREATOR = new Creator<Contest>() {
        @Override
        public Contest createFromParcel(Parcel in) {
            return new Contest(in);
        }

        @Override
        public Contest[] newArray(int size) {
            return new Contest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contestName);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeString(contestCode);
        parcel.writeInt(flag);
        parcel.writeTypedList(questions);
    }
}
