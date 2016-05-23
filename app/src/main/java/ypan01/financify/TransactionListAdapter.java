package ypan01.financify;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yang on 5/14/2016.
 */
public class TransactionListAdapter extends BaseAdapter {

    private Context m_context;

    private List<Transaction> m_transList;
    private List<Transaction> m_revTransList;

    public TransactionListAdapter(Context context, List<Transaction> transList) {
        //TODO
        m_context = context;
        m_transList = transList;
        m_revTransList = transList;
        Collections.reverse(m_revTransList);
    }

    @Override
    public int getCount() {
        return m_transList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_transList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /*if (convertView == null) {
            final Transaction trans = m_revTransList.get(position);
            TransactionView newView = new TransactionView(m_context, trans);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openDetails = new Intent(m_context, TransactionDetailActivity.class);
                    openDetails.putExtra("trans_id", trans.transactionId);
                    openDetails.putExtra("trans_isDeposit", trans.isDeposit);
                    openDetails.putExtra("trans_amount", trans.amount);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = df.format(trans.date);
                    openDetails.putExtra("trans_date", dateStr);
                    openDetails.putExtra("trans_cat", trans.category);
                    m_context.startActivity(openDetails);
                }
            });
            return newView;
        }
        else {
            TransactionView existingView = (TransactionView)convertView;
            existingView.setTransaction(m_transList.get(position));
            return existingView;
        }*/
        final Transaction trans = m_revTransList.get(position);
        TransactionView newView = new TransactionView(m_context, trans);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openDetails = new Intent(m_context, TransactionDetailActivity.class);
                openDetails.putExtra("trans_id", trans.transactionId);
                openDetails.putExtra("trans_isDeposit", trans.isDeposit);
                openDetails.putExtra("trans_amount", trans.amount);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = df.format(trans.date);
                openDetails.putExtra("trans_date", dateStr);
                openDetails.putExtra("trans_cat", trans.category);
                m_context.startActivity(openDetails);
            }
        });
        return newView;
    }
}
