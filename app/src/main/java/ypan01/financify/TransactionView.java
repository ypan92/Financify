package ypan01.financify;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by Yang on 5/14/2016.
 */
public class TransactionView extends LinearLayout {

    private TextView transTypeText;
    private TextView amountText;
    private TextView dateText;

    private Transaction transaction;


    public TransactionView(Context context, Transaction trans) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.trans_view, this, true);

        transTypeText = (TextView)findViewById(R.id.trans_type_text);
        amountText = (TextView)findViewById(R.id.trans_amount_text);
        dateText = (TextView)findViewById(R.id.trans_date);

        //amountText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);

        setTransaction(trans);
        requestLayout();
    }


    public void setTransaction(Transaction trans) {
        transaction = trans;
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedAmt = df.format(trans.amount);
        if (trans.isDeposit == 1) {
            transTypeText.setText("Transaction type: Deposit");
            amountText.setText("+" + formattedAmt);
        }
        else {
            transTypeText.setText("Transaction type: Withdraw");
            amountText.setText("-" + formattedAmt);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(trans.date);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        dateText.setText(month + "/" + day + "/" + year);

    }

    public Transaction getTransaction() {
        return transaction;
    }
}
