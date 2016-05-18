package ypan01.financify;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //public List<Transaction> transactions = new ArrayList<>();

    public static class DesignDemoFragment extends android.support.v4.app.Fragment {
        private static final String TAB_POSITION = "tab_position";

        private double withdrawTotal = 0;
        private double depositTotal = 0;

        private FinancifyService service;
        private Handler handler;
        private TransactionListAdapter transAdapter;
        private ListView transList;
        private List<Transaction> transactions = new ArrayList<>();
        private TextView totalBalanceView;
        private Button withdrawButton;
        private Button depositButton;
        private CurrencyEditText currencyEditText;
        private List<Transaction> m_trans = new ArrayList<>();
        private View root;

        private TextView tv;
        private BarGraph bg;

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
                root = inflater.inflate(R.layout.overview, container, false);
                tv = (TextView)root.findViewById(R.id.tv);
                bg = (BarGraph)root.findViewById(R.id.bg);
                //BarGraph bg2 = (BarGraph)root.findViewById(R.id.bg2);
                handler = new Handler(Looper.getMainLooper());

                //if (savedInstanceState == null) {
                    String baseURL = this.getResources().getString(R.string.api_url);
                    Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

                    service = client.create(FinancifyService.class);
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
                                    int isDeposit = transObj.getInt("isDeposit");
                                    if (isDeposit == 1) {
                                        depositTotal += amount;
                                    } else {
                                        withdrawTotal += amount;
                                    }
                                    String dateStr = transObj.getString("date");
                                    java.sql.Date date = java.sql.Date.valueOf(dateStr);
                                }
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        DecimalFormat df = new DecimalFormat("0.00");
                                        tv.setText("Total Balance: $" + df.format(depositTotal - withdrawTotal));
                                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        tv.setTextSize(20);

                                        ArrayList<Bar> points = new ArrayList<Bar>();
                                        Bar d = new Bar();
                                        d.setColor(Color.parseColor("#99CC00"));
                                        d.setName("Deposit");
                                        d.setValue((float) depositTotal);
                                        Bar d2 = new Bar();
                                        d2.setColor(Color.parseColor("#FFBB33"));
                                        d2.setName("Withdraw");
                                        d2.setValue((float) withdrawTotal);
                                        points.add(d);
                                        points.add(d2);

                                        bg.setBars(points);
                                        bg.setUnit("$");
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
                /*}
                else {
                    m_trans = savedInstanceState.getParcelableArrayList("key");
                    transactions = savedInstanceState.getParcelableArrayList("key");
                }*/

                return root;
            }
            else if (tabPosition == 1) {
                root  = inflater.inflate(R.layout.transactions, container, false);
                totalBalanceView = (TextView)root.findViewById(R.id.total_balance_amount);
                transAdapter = new TransactionListAdapter(root.getContext(), transactions);
                handler = new Handler(Looper.getMainLooper());
                transList = (ListView)root.findViewById(R.id.trans_list);
                transList.setAdapter(transAdapter);
                withdrawButton = (Button)root.findViewById(R.id.withdraw_button);
                depositButton = (Button)root.findViewById(R.id.deposit_button);
                currencyEditText = (CurrencyEditText)root.findViewById(R.id.currency_text);

                if (transactions.isEmpty() && withdrawTotal == 0 && depositTotal == 0) {
                    String baseURL = this.getResources().getString(R.string.api_url);
                    Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

                    service = client.create(FinancifyService.class);
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
                                    double amount = transObj.getDouble("amount");
                                    int isDeposit = transObj.getInt("isDeposit");
                                    if (isDeposit == 1) {
                                        depositTotal += amount;
                                    } else {
                                        withdrawTotal += amount;
                                    }
                                    String dateStr = transObj.getString("date");
                                    java.sql.Date date = java.sql.Date.valueOf(dateStr);

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
                }

                withdrawButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = currencyEditText.getText().toString();
                        if (!input.equals("")) {
                            input = input.substring(1, input.length());
                            double amount = Double.parseDouble(input);
                            if (amount > 0) {
                                java.sql.Date date = new java.sql.Date(new Date().getTime());
                                Transaction newTrans = new Transaction(0, amount, date);

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
                                InputMethodManager imm = (InputMethodManager) root.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                java.sql.Date date = new java.sql.Date(new Date().getTime());
                                Transaction newTrans = new Transaction(1, amount, date);

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
                                InputMethodManager imm = (InputMethodManager) root.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);
                            }
                        }
                    }
                });

                return root;
            }
            else if (tabPosition == 2) {
                root  = inflater.inflate(R.layout.categories, container, false);
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

    static class DesignDemoPagerAdapter extends FragmentStatePagerAdapter {

        private SparseArray<String> mPageReferenceMap = new SparseArray<>();

        public DesignDemoPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            DesignDemoFragment fragment = DesignDemoFragment.newInstance(position);
            mPageReferenceMap.put(position, "Tag" + position);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
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
        //viewPager.setCurrentItem(1);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Transaction> transArrList = new ArrayList<Transaction>(transactions);
        outState.putParcelableArrayList("key", transArrList);
    }*/
}
