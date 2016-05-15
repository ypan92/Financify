package ypan01.financify;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Yang on 5/14/2016.
 */
public class TransactionView extends LinearLayout {

    private TextView transTypeText;
    private TextView amountText;

    private Transaction transaction;


    public TransactionView(Context context, Transaction trans) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.trans_view, this, true);

        transTypeText = (TextView)findViewById(R.id.trans_type_text);
        amountText = (TextView)findViewById(R.id.trans_amount_text);

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

    }

    public Transaction getTransaction() {
        return transaction;
    }
}
