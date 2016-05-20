package ypan01.financify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TransactionView newView = new TransactionView(m_context, m_transList.get(position));
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
            return newView;
        }
        else {
            TransactionView existingView = (TransactionView)convertView;
            existingView.setTransaction(m_transList.get(position));
            return existingView;
        }
    }
}
