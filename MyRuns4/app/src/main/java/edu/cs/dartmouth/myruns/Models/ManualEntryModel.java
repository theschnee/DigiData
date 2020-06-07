package edu.cs.dartmouth.myruns.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ManualEntryModel implements Parcelable {
    public String title;
    public String data;

    public ManualEntryModel(String title, String data){
        this.title = title;
        this.data = data;
    }

    protected ManualEntryModel(Parcel in) {
        title = in.readString();
        data = in.readString();
    }

    public static final Parcelable.Creator<ManualEntryModel> CREATOR = new Parcelable.Creator<ManualEntryModel>() {
        @Override
        public ManualEntryModel createFromParcel(Parcel in) {
            return new ManualEntryModel(in);
        }

        @Override
        public ManualEntryModel[] newArray(int size) {
            return new ManualEntryModel[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data){
        this.data = data;

    }

    public String getTitle(){
        return title;

    }

    public void setTitle(String title){
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(data);

    }
}
