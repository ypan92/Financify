package ypan01.financify;

//import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Yang on 5/14/2016.
 */
public class Transaction implements Parcelable {
    public int userId;
    public int isDeposit;
    public double amount;
    public Date date;

    public Transaction() {
        userId = -1;
        amount = 0;
        date = null;
    }

    public Transaction(int isDeposit, double amount) {
        this.isDeposit = isDeposit;
        this.amount = amount;
    }

    public Transaction(int userId, int isDeposit, double amount) {
        this.userId = userId;
        this.isDeposit = isDeposit;
        this.amount = amount;
    }

    public Transaction(int isDeposit, double amount, Date date) {
        this.isDeposit = isDeposit;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isDeposit);
        dest.writeDouble(amount);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(date);
        dest.writeString(dateStr);
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            /*int isDep = source.readInt();
            double amt = source.readDouble();
            String dtStr = source.readString();
            Date dt = Date.valueOf(dtStr);
            return new Transaction(isDep, amt, dt);*/
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Transaction(Parcel source) {
        isDeposit = source.readInt();
        amount = source.readDouble();
        String dt = source.readString();
        date = Date.valueOf(dt);
    }
}
