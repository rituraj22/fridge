package fridge.site.tivra.fridgeforcodechef.DataModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by cogito on 12/17/17.
 */

public class Question implements Parcelable, Comparable<Question> {
    public String questionName;
    public String successDetails;
    public String contestCode;
    public String questionTitle;
    public String contestName;
    public int numSub;
    public String percent;
    public String questionCode;

    public Question(String question, String qcode, String submission, String percent, String contestCode, String contestName) {
        questionTitle = question;
        questionCode = qcode;
        try {
            numSub = Integer.parseInt(submission.trim());
        } catch (Exception e) {
            numSub = 0;
        }
        this.percent = percent;
        this.questionName = question + "  (" + qcode + ") ";
        this.successDetails = "Successful: " + submission + " (" + percent + "%) ";
        this.contestCode = contestCode;
        this.contestName = contestName;
    }

    protected Question(Parcel in) {
        questionName = in.readString();
        successDetails = in.readString();
        contestName = in.readString();
        questionTitle = in.readString();
        questionCode = in.readString();
        contestCode = in.readString();
        numSub = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questionName);
        parcel.writeString(successDetails);
        parcel.writeString(contestName);
        parcel.writeString(questionTitle);
        parcel.writeString(questionCode);
        parcel.writeString(contestCode);
        parcel.writeInt(numSub);
    }


    @Override
    public int compareTo(@NonNull Question question) {
        return (this.numSub - question.numSub);
    }
}
