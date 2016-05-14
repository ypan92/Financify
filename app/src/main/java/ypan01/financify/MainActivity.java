package ypan01.financify;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static class DesignDemoFragment extends android.support.v4.app.Fragment {
        private static final String TAB_POSITION = "tab_position";

        public DesignDemoFragment() {

        }

        public static DesignDemoFragment newInstance(int tabPosition) {
            DesignDemoFragment fragment = new DesignDemoFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            if (tabPosition == 0) {
                View root = inflater.inflate(R.layout.drawer_header, container, false);
                TextView tv = (TextView)root.findViewById(R.id.tv);
                BarGraph bg = (BarGraph)root.findViewById(R.id.bg);
                tv.setText("Total Balance: $2,403.25");
                tv.setTextColor(R.color.colorPrimary);
                tv.setTextSize(20);

                ArrayList<Bar> points = new ArrayList<Bar>();
                Bar d = new Bar();
                d.setColor(Color.parseColor("#99CC00"));
                d.setName("Test1");
                d.setValue(1200.15f);
                Bar d2 = new Bar();
                d2.setColor(Color.parseColor("#FFBB33"));
                d2.setName("Test2");
                d2.setValue(2203.10f);
                points.add(d);
                points.add(d2);

                bg.setBars(points);
                bg.setUnit("$");

                return root;
            }
            else if (tabPosition == 2) {
                PieGraph pg = new PieGraph(getActivity());
                PieSlice slice = new PieSlice();
                slice.setColor(Color.parseColor("#99CC00"));
                slice.setValue(2);
                pg.addSlice(slice);
                slice = new PieSlice();
                slice.setColor(Color.parseColor("#FFBB33"));
                slice.setValue(3);
                pg.addSlice(slice);
                slice = new PieSlice();
                slice.setColor(Color.parseColor("#AA66CC"));
                slice.setValue(8);
                pg.addSlice(slice);
                return pg;
            }
            else {
                /**TextView tv = new TextView(getActivity());
                tv.setGravity(Gravity.CENTER);
                tv.setText("Text in Tab #" + tabPosition);
                return tv;*/
                View root = inflater.inflate(R.layout.drawer_header, container, false);
                TextView tv = (TextView)root.findViewById(R.id.tv);
                BarGraph bg = (BarGraph)root.findViewById(R.id.bg);
                tv.setText("Text in Tab #" + tabPosition);
                tv.setTextColor(R.color.colorPrimary);

                ArrayList<Bar> points = new ArrayList<Bar>();
                Bar d = new Bar();
                d.setColor(Color.parseColor("#99CC00"));
                d.setName("Test1");
                d.setValue(10);
                Bar d2 = new Bar();
                d2.setColor(Color.parseColor("#FFBB33"));
                d2.setName("Test2");
                d2.setValue(20);
                points.add(d);
                points.add(d2);

                bg.setBars(points);
                bg.setUnit("$");

                return root;
            }
        }
    }

    static class DesignDemoPagerAdapter extends FragmentStatePagerAdapter {
        public DesignDemoPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return DesignDemoFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Overview";
            }
            else if (position == 1) {
                return "Transactions";
            }
            else if (position == 2) {
                return "Categories";
            }
            return "Tab " + position;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DesignDemoPagerAdapter adapter = new DesignDemoPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        /*ArrayList<Bar> points = new ArrayList<Bar>();
        Bar d = new Bar();
        d.setColor(Color.parseColor("#99CC00"));
        d.setName("Test1");
        d.setValue(10);
        Bar d2 = new Bar();
        d2.setColor(Color.parseColor("#FFBB33"));
        d2.setName("Test2");
        d2.setValue(20);
        points.add(d);
        points.add(d2);

        BarGraph g = (BarGraph)findViewById(R.id.graph);
        g.setBars(points);
        g.setUnit("$");*/
    }
}
