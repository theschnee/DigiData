package edu.cs.dartmouth.myruns.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryModel implements Parcelable {
    public String title;
    public String data;
    public String date;
    public String entryType;
    public String duration;
    public String id;

    public HistoryModel(String title, String data, String date, String entryType, String duration, String id) {
        this.title = title;
        this.data = data;
        this.date = date;
        this.entryType = entryType;
        this.duration = duration;
        this.id = id;
    }

    public HistoryModel(Parcel in) {
        title = in.readString();
        data = in.readString();
        date = in.readString();
        entryType = in.readString();
        duration = in.readString();
        id = in.readString();
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String Date) {
        this.date = date;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(data);
        dest.writeString(date);
        dest.writeString(entryType);
        dest.writeString(duration);
        dest.writeString(id);
    }
}
