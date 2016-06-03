package ypan01.financify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ypan01.financify.Events.SendLabelClickEvent;

/**
 * Created by Yang on 5/23/2016.
 */
public class CategoryListAdapter extends BaseAdapter {
    private Context m_context;
    private List<CategoryLabel> categories;
    private Bus mBus;

    public CategoryListAdapter(Context context, List<CategoryLabel> categories) {
        m_context = context;
        this.categories = categories;
        mBus = BusProvider.bus();
        mBus.register(this);

    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final CategoryView newView = new CategoryView(m_context, categories.get(position));
            /*newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryLabel clickedLabel = categories.get(position);
                    if (clickedLabel.name.equals("Uncategorized")) {
                        mBus.post(new SendLabelClickEvent(clickedLabel.name));
                    }
                    else if (clickedLabel.name.equals("Food")) {

                    }
                    else if (clickedLabel.name.equals("Gas")) {

                    }
                    else if (clickedLabel.name.equals("Clothes")) {

                    }
                    else if (clickedLabel.name.equals("Technology")) {

                    }
                    else if (clickedLabel.name.equals("Kitchen Hardware")) {

                    }
                    else if (clickedLabel.name.equals("Furniture")) {

                    }
                }
            });*/
            return newView;
        }
        else {
            CategoryView existingView = (CategoryView)convertView;
            existingView.setCategoryLabel(categories.get(position));
            /*existingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryLabel clickedLabel = categories.get(position);
                    if (clickedLabel.name.equals("Uncategorized")) {
                        mBus.post(new SendLabelClickEvent(clickedLabel.name));
                    }
                    else if (clickedLabel.name.equals("Food")) {

                    }
                    else if (clickedLabel.name.equals("Gas")) {

                    }
                    else if (clickedLabel.name.equals("Clothes")) {

                    }
                    else if (clickedLabel.name.equals("Technology")) {

                    }
                    else if (clickedLabel.name.equals("Kitchen Hardware")) {

                    }
                    else if (clickedLabel.name.equals("Furniture")) {

                    }
                }
            });*/
            return existingView;
        }
    }
}
