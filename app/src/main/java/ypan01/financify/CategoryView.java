package ypan01.financify;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Yang on 5/23/2016.
 */
public class CategoryView extends LinearLayout {

    private ImageView imageView;
    private TextView labelName;

    private CategoryLabel categoryLabel;

    public CategoryView(Context context, CategoryLabel categoryLabel) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cat_view, this, true);

        imageView = (ImageView)findViewById(R.id.cat_color_key);
        labelName = (TextView)findViewById(R.id.cat_name_key);

        setCategoryLabel(categoryLabel);

        requestLayout();
    }

    public void setCategoryLabel(CategoryLabel categoryLabel) {
        imageView.setBackgroundColor(Color.parseColor(categoryLabel.color));
        DecimalFormat df = new DecimalFormat("0.00");
        labelName.setText(categoryLabel.name + " (" + df.format(categoryLabel.percent) + "%) $" + df.format(categoryLabel.amount));
    }

}
