package study.itmo.xpech.mdft.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.JsonNode;

public class Pic implements Parcelable {
    public final String id;
    public final String description;
    public final String srcUrl;

    public Pic(String id, String description, String srcUrl) {
        this.id = id;
        this.description = description;
        this.srcUrl = srcUrl;
    }

    public Pic(int id, String description, String srcUrl) {
        this.id = String.valueOf(id);
        this.description = description;
        this.srcUrl = srcUrl;
    }

    public Pic(int id, JsonNode pic) {
        this.id = String.valueOf(id);
        description = pic.get("title").asText();
        srcUrl = getFullPls(pic.get("media").get("m").asText());
    }

    private static String getFullPls(String pureUrl) {
        String res = pureUrl.substring(0, pureUrl.length() - 5);
        return res + "c.jpg";
    }

    String getDescription() {return description;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(srcUrl);
    }

    private Pic(Parcel parcel) {
        id = parcel.readString();
        description = parcel.readString();
        srcUrl = parcel.readString();
    }

    public static final Parcelable.Creator<Pic> CREATOR = new Parcelable.Creator<Pic>() {
        public Pic createFromParcel(Parcel in) {return new Pic(in);}
        public Pic[] newArray(int size) {return new Pic[size];}
    };


    @Override
    public String toString() {
        return description;
    }
}
