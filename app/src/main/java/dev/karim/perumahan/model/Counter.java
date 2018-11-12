
package dev.karim.perumahan.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Counter implements Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("counter")
    @Expose
    private int counter;
    @SerializedName("createdAt")
    @Expose
    private Object createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Object updatedAt;
    public final static Parcelable.Creator<Counter> CREATOR = new Creator<Counter>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Counter createFromParcel(Parcel in) {
            Counter instance = new Counter();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.counter = ((int) in.readValue((int.class.getClassLoader())));
            instance.createdAt = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.updatedAt = ((Object) in.readValue((Object.class.getClassLoader())));
            return instance;
        }

        public Counter[] newArray(int size) {
            return (new Counter[size]);
        }

    }
    ;

    /**
     * 
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * 
     * @param counter
     *     The counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     *
     * @return
     *     The createdAt
     */
    public Object getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The createdAt
     */
    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public Object getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updatedAt
     */
    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(counter);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
    }

    public int describeContents() {
        return  0;
    }

}
