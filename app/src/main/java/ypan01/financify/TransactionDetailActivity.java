package ypan01.financify;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.sql.Date;

/**
 * Created by Yang on 5/16/2016.
 */
public class TransactionDetailActivity extends AppCompatActivity {

    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_detail);

        TextView tv = (TextView)findViewById(R.id.trans_det_amt);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                transaction = null;
            }
            else {
                int isDeposit = extras.getInt("trans_isDeposit");
                double amount = extras.getDouble("trans_amount");
                String dateStr = extras.getString("trans_date");
                Date date = Date.valueOf(dateStr);
                transaction = new Transaction(isDeposit, amount, date);
            }
        }
        else {
            int isDeposit = (int) savedInstanceState.getSerializable("trans_isDeposit");
            double amount = (double) savedInstanceState.getSerializable("trans_amount");
            String dateStr = (String) savedInstanceState.getSerializable("trans_date");
            Date date = Date.valueOf(dateStr);
            transaction = new Transaction(isDeposit, amount, date);
        }

        tv.setText("Transaction type(" + transaction.isDeposit + ") has amount " + transaction.amount + " on " + transaction.date);
    }
}
