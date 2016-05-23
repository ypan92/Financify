package ypan01.financify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yang on 5/23/2016.
 */
public class CategoryListAdapter extends BaseAdapter {
    private Context m_context;
    private List<CategoryLabel> categories;

    public CategoryListAdapter(Context context, List<CategoryLabel> categories) {
        m_context = context;
        this.categories = categories;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            CategoryView newView = new CategoryView(m_context, categories.get(position));
            return newView;
        }
        else {
            CategoryView existingView = (CategoryView)convertView;
            existingView.setCategoryLabel(categories.get(position));
            return existingView;
        }
    }
}
