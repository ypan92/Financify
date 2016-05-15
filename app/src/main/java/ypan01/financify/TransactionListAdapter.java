package ypan01.financify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Yang on 5/14/2016.
 */
public class TransactionListAdapter extends BaseAdapter {

    private Context m_context;

    private List<Transaction> m_transList;

    public TransactionListAdapter(Context context, List<Transaction> transList) {
        //TODO
        m_context = context;
        m_transList = transList;
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
            return newView;
        }
        else {
            TransactionView existingView = (TransactionView)convertView;
            existingView.setTransaction(m_transList.get(position));
            return existingView;
        }
    }
}
