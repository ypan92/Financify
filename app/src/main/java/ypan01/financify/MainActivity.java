package ypan01.financify;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ypan01.financify.Holograph.Bar;
import ypan01.financify.Holograph.BarGraph;
import ypan01.financify.Holograph.PieGraph;
import ypan01.financify.Holograph.PieSlice;

public class MainActivity extends AppCompatActivity {

    public static class TabFragment extends android.support.v4.app.Fragment {
        private static final String TAB_POSITION = "tab_position";

        private double withdrawTotal = 0;
        private double depositTotal = 0;

        private TransactionService service;
        private Handler handler;

        // elements of transactions tab
        private TransactionListAdapter transAdapter;
        private ListView transList;
        private List<Transaction> transactions = new ArrayList<>();
        private TextView totalBalanceView;
        private Button withdrawButton;
        private Button depositButton;
        private CurrencyEditText currencyEditText;

        // elements of overview tab
        private TextView lastMonthTotal;
        private TextView thisMonthTotal;
        private BarGraph lastMonthGraph;
        private BarGraph thisMonthGraph;

        double uncategorizedTotal = 0;
        double foodTotal = 0;
        double gasTotal = 0;
        double clothesTotal = 0;
        double techTotal = 0;
        double kitchenTotal = 0;
        double furnitureTotal = 0;

        //elements of categories tab
        private PieGraph pg;
        private ListView catLabels;
        private CategoryListAdapter catAdapter;
        private List<CategoryLabel> categoryLabels = new ArrayList<>();

        public TabFragment() {

        }

        public static TabFragment newInstance(int tabPosition) {
            TabFragment fragment = new TabFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        protected String getMonth(int month) {
            switch (month) {
                case 0:
                    return "January";
                case 1:
                    return "February";
                case 2:
                    return "March";
                case 3:
                    return "April";
                case 4:
                    return "May";
                case 5:
                    return "June";
                case 6:
                    return "July";
                case 7:
                    return "August";
                case 8:
                    return "September";
                case 9:
                    return "October";
                case 10:
                    return "November";
                case 11:
                    return "December";
                default:
                    return "Not a month";
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            if (tabPosition == 0) {
                View root = inflater.inflate(R.layout.overview, container, false);
                lastMonthTotal = (TextView)root.findViewById(R.id.tv);
                lastMonthGraph = (BarGraph)root.findViewById(R.id.bg);
                thisMonthTotal = (TextView)root.findViewById(R.id.tv2);
                thisMonthGraph = (BarGraph)root.findViewById(R.id.bg2);

                Date currentDate = new Date(new java.util.Date().getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                int month = cal.get(Calendar.MONTH);
                final int year = cal.get(Calendar.YEAR);

                final String lastMonth = getMonth(month - 1);
                final String thisMonth = getMonth(month);

                handler = new Handler(Looper.getMainLooper());
                String baseURL = this.getResources().getString(R.string.api_url);
                Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();
                service = client.create(TransactionService.class);

                Call<ResponseBody> lastMonthTransCall = service.getMonthTransactions(month, year);
                lastMonthTransCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        depositTotal = 0;
                        withdrawTotal = 0;
                        try {
                            String body = response.body().string();
                            JSONObject responseObj = new JSONObject(body);
                            int numTrans = responseObj.length();
                            for (int i = 0; i < numTrans; i++) {
                                JSONObject transObj = responseObj.getJSONObject("" + i);
                                int isDeposit = transObj.getInt("isDeposit");
                                double amount = transObj.getDouble("amount");
                                if (isDeposit == 1) {
                                    depositTotal += amount;
                                } else {
                                    withdrawTotal += amount;
                                }
                                String dateStr = transObj.getString("date");
                                Date date = Date.valueOf(dateStr);
                            }

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    lastMonthTotal.setText(lastMonth + " " + year + " Net Balance: $" + df.format(depositTotal - withdrawTotal));
                                    lastMonthTotal.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    lastMonthTotal.setTextSize(20);

                                    ArrayList<Bar> lastMonthPoints = new ArrayList<Bar>();
                                    Bar lastDepositBar = new Bar();
                                    lastDepositBar.setColor(Color.parseColor("#99CC00"));
                                    lastDepositBar.setName("Deposit");
                                    lastDepositBar.setValue((float) depositTotal);
                                    Bar lastWithdrawBar = new Bar();
                                    lastWithdrawBar.setColor(Color.parseColor("#FFBB33"));
                                    lastWithdrawBar.setName("Withdraw");
                                    lastWithdrawBar.setValue((float) withdrawTotal);
                                    lastMonthPoints.add(lastDepositBar);
                                    lastMonthPoints.add(lastWithdrawBar);

                                    lastMonthGraph.setBars(lastMonthPoints);
                                    lastMonthGraph.setUnit("$");

                                    startActivity(getActivity().getIntent());

                                }
                            };
                            handler.post(runnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });

                Call<ResponseBody> thisMonthCall = service.getMonthTransactions(month+1, year);
                thisMonthCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        depositTotal = 0;
                        withdrawTotal = 0;
                        try {
                            String body = response.body().string();
                            JSONObject responseObj = new JSONObject(body);
                            int numTrans = responseObj.length();
                            for (int i = 0; i < numTrans; i++) {
                                JSONObject transObj = responseObj.getJSONObject("" + i);
                                int isDeposit = transObj.getInt("isDeposit");
                                double amount = transObj.getDouble("amount");
                                if (isDeposit == 1) {
                                    depositTotal += amount;
                                } else {
                                    withdrawTotal += amount;
                                }
                                String dateStr = transObj.getString("date");
                                Date date = Date.valueOf(dateStr);
                            }

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    thisMonthTotal.setText(thisMonth + " " + year + " Net Balance: $" + df.format(depositTotal - withdrawTotal));
                                    thisMonthTotal.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    thisMonthTotal.setTextSize(20);

                                    ArrayList<Bar> thisMonthPoints = new ArrayList<Bar>();
                                    Bar thisDepositBar = new Bar();
                                    thisDepositBar.setColor(Color.parseColor("#99CC00"));
                                    thisDepositBar.setName("Deposit");
                                    thisDepositBar.setValue((float) depositTotal);
                                    Bar thisWithdrawBar = new Bar();
                                    thisWithdrawBar.setColor(Color.parseColor("#FFBB33"));
                                    thisWithdrawBar.setName("Withdraw");
                                    thisWithdrawBar.setValue((float) withdrawTotal);
                                    thisMonthPoints.add(thisDepositBar);
                                    thisMonthPoints.add(thisWithdrawBar);

                                    thisMonthGraph.setBars(thisMonthPoints);
                                    thisMonthGraph.setUnit("$");

                                    startActivity(getActivity().getIntent());

                                }
                            };
                            handler.post(runnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });

                return root;
            }
            else if (tabPosition == 1) {
                View root  = inflater.inflate(R.layout.transactions, container, false);
                totalBalanceView = (TextView)root.findViewById(R.id.total_balance_amount);
                transAdapter = new TransactionListAdapter(root.getContext(), transactions);
                handler = new Handler(Looper.getMainLooper());
                transList = (ListView)root.findViewById(R.id.trans_list);
                transList.setAdapter(transAdapter);
                withdrawButton = (Button)root.findViewById(R.id.withdraw_button);
                depositButton = (Button)root.findViewById(R.id.deposit_button);
                currencyEditText = (CurrencyEditText)root.findViewById(R.id.currency_text);

                String baseURL = this.getResources().getString(R.string.api_url);
                Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

                service = client.create(TransactionService.class);
                Call<ResponseBody> call = service.getTransactions();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String body = response.body().string();
                            JSONObject responseObj = new JSONObject(body);
                            int numTransactions = responseObj.length();
                            for (int i = 0; i < numTransactions; i++) {
                                JSONObject transObj = responseObj.getJSONObject("" + i);
                                int transactionId = transObj.getInt("transactionId");
                                //int userId = transObj.getInt("userId");
                                int isDeposit = transObj.getInt("isDeposit");
                                double amount = transObj.getDouble("amount");
                                if (isDeposit == 1) {
                                    depositTotal += amount;
                                } else {
                                    withdrawTotal += amount;
                                }
                                String dateStr = transObj.getString("date");
                                Date date = Date.valueOf(dateStr);
                                int category = transObj.getInt("category");

                                Transaction trans = new Transaction(transactionId, isDeposit, amount, date, category);
                                transactions.add(trans);
                                transAdapter.notifyDataSetChanged();
                            }
                            Collections.reverse(transactions);
                            transAdapter.notifyDataSetChanged();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    totalBalanceView.setText(df.format(depositTotal - withdrawTotal));
                                }
                            };
                            handler.post(runnable);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });

                withdrawButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = currencyEditText.getText().toString();
                        if (!input.equals("")) {
                            input = input.substring(1, input.length());
                            double amount = Double.parseDouble(input);
                            if (amount > 0) {
                                Transaction newTrans = new Transaction(0, amount);

                                transactions.add(0, newTrans);
                                transAdapter.notifyDataSetChanged();

                                withdrawTotal += amount;
                                DecimalFormat df = new DecimalFormat("0.00");
                                totalBalanceView.setText(df.format(depositTotal - withdrawTotal));

                                Call<ResponseBody> createCall = service.createTransaction(newTrans.isDeposit, newTrans.amount, newTrans.date);
                                createCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {

                                        } else {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.d("Error", t.getMessage());
                                    }
                                });

                                currencyEditText.setText("$0.00");
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);
                            }
                        }
                    }
                });

                depositButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = currencyEditText.getText().toString();
                        if (!input.equals("")) {
                            input = input.substring(1, input.length());
                            double amount = Double.parseDouble(input);
                            if (amount > 0) {
                                Transaction newTrans = new Transaction(1, amount);

                                transactions.add(0, newTrans);
                                transAdapter.notifyDataSetChanged();

                                depositTotal += amount;
                                DecimalFormat df = new DecimalFormat("0.00");
                                totalBalanceView.setText(df.format(depositTotal - withdrawTotal));

                                Call<ResponseBody> createCall = service.createTransaction(newTrans.isDeposit, newTrans.amount, newTrans.date);
                                createCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {

                                        } else {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.d("Error", t.getMessage());
                                    }
                                });

                                currencyEditText.setText("$0.00");
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);
                            }
                        }
                    }
                });

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);

                return root;
            }
            else if (tabPosition == 2) {
                View root = inflater.inflate(R.layout.categories, container, false);
                pg = (PieGraph)root.findViewById(R.id.pg);
                catLabels = (ListView)root.findViewById(R.id.category_labels);
                /*PieSlice slice = new PieSlice();
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
                pg.addSlice(slice);*/

                handler = new Handler(Looper.getMainLooper());
                catAdapter = new CategoryListAdapter(root.getContext(), categoryLabels);
                catLabels.setAdapter(catAdapter);

                CategoryLabel noLabel = new CategoryLabel("#1188AA", "Uncategorized");
                categoryLabels.add(noLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel foodLabel = new CategoryLabel("#FFBB33", "Food");
                categoryLabels.add(foodLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel gasLabel = new CategoryLabel("#AA66CC", "Gas");
                categoryLabels.add(gasLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel clothesLabel = new CategoryLabel("#77DD11", "Clothes");
                categoryLabels.add(clothesLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel techLabel = new CategoryLabel("#33AA55", "Technology");
                categoryLabels.add(techLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel kitchenLabel = new CategoryLabel("#DD00FF", "Kitchen Hardware");
                categoryLabels.add(kitchenLabel);
                catAdapter.notifyDataSetChanged();

                CategoryLabel furnitureLabel = new CategoryLabel("#99CC00", "Furniture");
                categoryLabels.add(furnitureLabel);
                catAdapter.notifyDataSetChanged();

                String baseURL = this.getResources().getString(R.string.api_url);
                Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

                service = client.create(TransactionService.class);
                Call<ResponseBody> call = service.getTransactions();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String body = response.body().string();
                            JSONObject responseObj = new JSONObject(body);
                            int numTransactions = responseObj.length();
                            for (int i = 0; i < numTransactions; i++) {
                                JSONObject transObj = responseObj.getJSONObject("" + i);
                                double amount = transObj.getDouble("amount");
                                int category = transObj.getInt("category");

                                if (category == 0) {
                                    uncategorizedTotal += amount;
                                } else if (category == 1) {
                                    foodTotal += amount;
                                } else if (category == 2) {
                                    gasTotal += amount;
                                } else if (category == 3) {
                                    clothesTotal += amount;
                                } else if (category == 4) {
                                    techTotal += amount;
                                } else if (category == 5) {
                                    kitchenTotal += amount;
                                } else if (category == 6) {
                                    furnitureTotal += amount;
                                }
                            }
                            Collections.reverse(transactions);
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (uncategorizedTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#1188AA"));
                                        slice.setValue((float)uncategorizedTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (foodTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#FFBB33"));
                                        slice.setValue((float)foodTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (gasTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#AA66CC"));
                                        slice.setValue((float)gasTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (clothesTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#77DD11"));
                                        slice.setValue((float)clothesTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (techTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#33AA55"));
                                        slice.setValue((float)techTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (kitchenTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#DD00FF"));
                                        slice.setValue((float)kitchenTotal);
                                        pg.addSlice(slice);
                                    }
                                    if (furnitureTotal > 0) {
                                        PieSlice slice = new PieSlice();
                                        slice.setColor(Color.parseColor("#99CC00"));
                                        slice.setValue((float) furnitureTotal);
                                        pg.addSlice(slice);
                                    }
                                }
                            };
                            handler.post(runnable);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
                return root;
            }
            else {
                TextView tv = new TextView(getActivity());
                tv.setGravity(Gravity.CENTER);
                tv.setText("Text in Tab #" + tabPosition);
                return tv;
            }
        }
    }

    static class TabPagerAdapter extends FragmentStatePagerAdapter {
        public TabPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return TabFragment.newInstance(position);
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

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(2);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
