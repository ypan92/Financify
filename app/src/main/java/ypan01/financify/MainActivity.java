package ypan01.financify;

import android.content.Context;
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

        public TabFragment() {

        }

        public static TabFragment newInstance(int tabPosition) {
            TabFragment fragment = new TabFragment();
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
                View root = inflater.inflate(R.layout.overview, container, false);
                lastMonthTotal = (TextView)root.findViewById(R.id.tv);
                lastMonthGraph = (BarGraph)root.findViewById(R.id.bg);
                thisMonthTotal = (TextView)root.findViewById(R.id.tv2);
                thisMonthGraph = (BarGraph)root.findViewById(R.id.bg2);

                Date currentDate = new Date(new java.util.Date().getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

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
                                    lastMonthTotal.setText("Total Balance: $" + df.format(depositTotal - withdrawTotal));
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
                                    thisMonthTotal.setText("Total Balance: $" + df.format(depositTotal - withdrawTotal));
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

                                Transaction trans = new Transaction(isDeposit, amount, date);
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

                return root;
            }
            else if (tabPosition == 2) {
                View root = inflater.inflate(R.layout.categories, container, false);
                PieGraph pg = (PieGraph)root.findViewById(R.id.pg);
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
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
