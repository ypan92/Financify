package ypan01.financify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
import ypan01.financify.Events.GetCategoryTotalEvent;
import ypan01.financify.Events.GetFullMonthTransactionEvent;
import ypan01.financify.Events.GetMonthCategoryTotalEvent;
import ypan01.financify.Events.GetMonthTransactionEvent;
import ypan01.financify.Events.GetTransactionEvent;
import ypan01.financify.Events.SendAmountTotalEvent;
import ypan01.financify.Events.SendCategoryTotalEvent;
import ypan01.financify.Events.SendLabelClickEvent;
import ypan01.financify.Events.SendMonthBalanceEvent;
import ypan01.financify.Events.SendTransactionEvent;
import ypan01.financify.Holograph.Bar;
import ypan01.financify.Holograph.BarGraph;
import ypan01.financify.Holograph.PieGraph;
import ypan01.financify.Holograph.PieSlice;

public class MainActivity extends AppCompatActivity {

    private static Bus mBus = BusProvider.bus();
    private static ApiClient mApiClient = ApiClient.getInstance();

    private static TransactionListAdapter transAdapter;
    private static List<Transaction> transactions = new ArrayList<>();

    private static TextView totalBalanceView;
    private static Button withdrawButton;
    private static Button depositButton;
    private static CurrencyEditText currencyEditText;
    private static Spinner transSpinner;

    private static TextView lastMonthTotal;
    private static TextView thisMonthTotal;
    private static BarGraph lastMonthGraph;
    private static BarGraph thisMonthGraph;

    private static PieGraph pg;
    private static CategoryListAdapter catAdapter;
    private static List<CategoryLabel> categoryLabels = new ArrayList<>();
    private static Spinner catSpinner;

    private static double uncategorizedTotal = 0;
    private static double foodTotal = 0;
    private static double gasTotal = 0;
    private static double clothesTotal = 0;
    private static double techTotal = 0;
    private static double kitchenTotal = 0;
    private static double furnitureTotal = 0;
    private static double total = 0;

    private static double withdrawTotal = 0;
    private static double depositTotal = 0;

    private static boolean gotMonths = false;
    private static boolean registeredBus = false;

    private static MonthStringUtil util = new MonthStringUtil();

    private static TextView totalTitle;

    private static PieSlice uncategorizedSlice;
    private static PieSlice foodSlice;
    private static PieSlice gasSlice;
    private static PieSlice clothesSlice;
    private static PieSlice technologySlice;
    private static PieSlice kitchenSlice;
    private static PieSlice furnitureSlice;
    private static ArrayList<PieSlice> pgSlices = new ArrayList<>();

    private static Spinner typeSpinner;
    private static List<Transaction> masterFilterList = new ArrayList<>();
    private static TextView withdrawTotalView;
    private static TextView depositTotalView;

    public static class TabFragment extends android.support.v4.app.Fragment {
        private static final String TAB_POSITION = "tab_position";

        private TransactionService service;
        private Handler handler;

        private ListView transList;

        private ListView catLabels;

        public TabFragment() {

        }

        public static TabFragment newInstance(int tabPosition) {
            TabFragment fragment = new TabFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putDouble("withdrawTotal", withdrawTotal);
            outState.putDouble("depositTotal", depositTotal);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                withdrawTotal = savedInstanceState.getDouble("withdrawTotal");
                depositTotal = savedInstanceState.getDouble("depositTotal");
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            Date currentDate = new Date(new java.util.Date().getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            final int month = cal.get(Calendar.MONTH);
            final int year = cal.get(Calendar.YEAR);

            if (tabPosition == 0) {
                View root = inflater.inflate(R.layout.overview, container, false);
                lastMonthTotal = (TextView)root.findViewById(R.id.tv);
                lastMonthGraph = (BarGraph)root.findViewById(R.id.bg);
                thisMonthTotal = (TextView)root.findViewById(R.id.tv2);
                thisMonthGraph = (BarGraph)root.findViewById(R.id.bg2);

                mBus.post(new GetMonthTransactionEvent(month, year, lastMonthTotal, lastMonthGraph));
                mBus.post(new GetMonthTransactionEvent(month + 1, year, thisMonthTotal, thisMonthGraph));

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
                transSpinner = (Spinner)root.findViewById(R.id.trans_time_picker);
                typeSpinner = (Spinner)root.findViewById(R.id.type_spinner);

                withdrawTotalView = (TextView)root.findViewById(R.id.withdraw_balance_amount);
                depositTotalView = (TextView)root.findViewById(R.id.deposit_balance_amount);

                transSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        transactions.clear();
                        transAdapter.notifyDataSetChanged();
                        masterFilterList.clear();
                        typeSpinner.setSelection(0);

                        if (position == 0) {
                            mBus.post(new GetTransactionEvent());
                        } else if (position == 1) {
                            mBus.post(new GetFullMonthTransactionEvent(month, year));
                        } else if (position == 2) {
                            mBus.post(new GetFullMonthTransactionEvent(month + 1, year));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                withdrawButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String input = currencyEditText.getText().toString();
                        if (!input.equals("")) {
                            input = input.substring(1, input.length());
                            double amount = Double.parseDouble(input);
                            if (amount > 0) {
                                Date date = new Date(new java.util.Date().getTime());
                                Transaction newTrans = new Transaction(0, amount, date);

                                transactions.add(0, newTrans);
                                masterFilterList.add(newTrans);
                                transAdapter.notifyDataSetChanged();

                                withdrawTotal += amount;
                                DecimalFormat df = new DecimalFormat("0.00");
                                totalBalanceView.setText(df.format(depositTotal - withdrawTotal));
                                //withdrawTotalView.setText("$" + df.format(withdrawTotal));

                                Call<ResponseBody> createCall = mApiClient.getTransactionService().createTransaction(newTrans.isDeposit, newTrans.amount, newTrans.date);
                                createCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        transSpinner.setSelection(0);
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
                                Date date = new Date(new java.util.Date().getTime());
                                Transaction newTrans = new Transaction(1, amount, date);

                                transactions.add(0, newTrans);
                                masterFilterList.add(newTrans);
                                transAdapter.notifyDataSetChanged();

                                depositTotal += amount;
                                DecimalFormat df = new DecimalFormat("0.00");
                                totalBalanceView.setText(df.format(depositTotal - withdrawTotal));
                                //depositTotalView.setText("$" + df.format(depositTotal));

                                Call<ResponseBody> createCall = mApiClient.getTransactionService().createTransaction(newTrans.isDeposit, newTrans.amount, newTrans.date);
                                createCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        transSpinner.setSelection(0);
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

                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        transactions.clear();
                        transAdapter.notifyDataSetChanged();
                        if (position == 0) {
                            for (Transaction trans : masterFilterList) {
                                transactions.add(trans);
                            }
                            Collections.reverse(transactions);
                            transAdapter.notifyDataSetChanged();
                        }
                        else if (position == 1) {
                            for (Transaction trans : masterFilterList) {
                                if (trans.isDeposit == 0) {
                                    transactions.add(trans);
                                }
                            }
                            Collections.reverse(transactions);
                            transAdapter.notifyDataSetChanged();
                        }
                        else if (position == 2) {
                            for (Transaction trans : masterFilterList) {
                                if (trans.isDeposit == 1) {
                                    transactions.add(trans);
                                }
                            }
                            Collections.reverse(transactions);
                            transAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

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
                catSpinner = (Spinner)root.findViewById(R.id.cat_time_picker);

                catAdapter = new CategoryListAdapter(root.getContext(), categoryLabels);
                catLabels.setAdapter(catAdapter);

                final TextView spendingTitle = (TextView)root.findViewById(R.id.spending_title);
                totalTitle = (TextView)root.findViewById(R.id.total_title);

                catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String titleEnding = "Spending Report By Category";
                        spendingTitle.setGravity(Gravity.CENTER);
                        spendingTitle.setTextSize(24);
                        spendingTitle.setPadding(15, 15, 15, 15);
                        //TextView tv = (TextView)view;
                        //tv.setGravity(Gravity.CENTER);

                        totalTitle.setGravity(Gravity.CENTER);
                        totalTitle.setPadding(0, 0, 0, 75);
                        catLabels.setPadding(0, 75, 0, 0);

                        if (position == 0) {
                            spendingTitle.setText("All Time " + titleEnding);
                            mBus.post(new GetCategoryTotalEvent());
                        } else if (position == 1) {
                            spendingTitle.setText(util.getMonth(month-1) + " " + year + " " + titleEnding);
                            mBus.post(new GetMonthCategoryTotalEvent(month, year));
                        } else if (position == 2) {
                            spendingTitle.setText(util.getMonth(month) + " " + year + " " + titleEnding);
                            mBus.post(new GetMonthCategoryTotalEvent(month + 1, year));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

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

        private Fragment overview;
        private Fragment transaction;
        private Fragment category;

        public TabPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return TabFragment.newInstance(position);
            /*switch (position) {
                case 0:
                    if (overview == null) {
                        overview = TabFragment.newInstance(position);
                    }
                    return overview;
                case 1:
                    if (transaction == null) {
                        transaction = TabFragment.newInstance(position);
                    }
                    return transaction;
                case 2:
                    if (category == null) {
                        category = TabFragment.newInstance(position);
                    }
                    return category;
                default:
                    return TabFragment.newInstance(position);
            }*/
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!registeredBus) {
            mBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBus.register(this);
        registeredBus = true;
        transactions.clear();
        transAdapter.notifyDataSetChanged();

        Date currentDate = new Date(new java.util.Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        final int month = cal.get(Calendar.MONTH);
        final int year = cal.get(Calendar.YEAR);

        String listScope = transSpinner.getSelectedItem().toString();
        if (listScope.equals("All Time")) {
            mBus.post(new GetTransactionEvent());
        }
        else if (listScope.equals("Last Month")) {
            mBus.post(new GetFullMonthTransactionEvent(month, year));
        }
        else if (listScope.equals("This Month")) {
            mBus.post(new GetFullMonthTransactionEvent(month + 1, year));
        }
    }

    @Subscribe
    public void onGetTransactionsEvent(GetTransactionEvent event) {
        Call<ResponseBody> getTransactionsCall = mApiClient.getTransactionService().getTransactions();
        getTransactionsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                double depositTotal = 0;
                double withdrawTotal = 0;
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
                        int category = transObj.getInt("category");
                        String dateStr = transObj.getString("date");
                        Transaction trans;
                        if (dateStr != null) {
                            Date date = Date.valueOf(dateStr);
                            trans = new Transaction(transactionId, isDeposit, amount, date, category);
                        }
                        else {
                            trans = new Transaction(transactionId, isDeposit, amount, category);
                        }
                        mBus.post(new SendTransactionEvent(trans));
                    }
                    mBus.post(new SendAmountTotalEvent(withdrawTotal, depositTotal));
                    Collections.reverse(transactions);
                    transAdapter.notifyDataSetChanged();
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

    private void resetTotals() {
        uncategorizedTotal = 0;
        foodTotal = 0;
        gasTotal = 0;
        clothesTotal = 0;
        techTotal = 0;
        kitchenTotal = 0;
        furnitureTotal = 0;
        total = 0;
    }

    @Subscribe
    public void onGetCategoryTotalEvent(GetCategoryTotalEvent event) {
        Call<ResponseBody> getTransactionCall = mApiClient.getTransactionService().getTransactions();
        getTransactionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resetTotals();
                try {
                    String body = response.body().string();
                    JSONObject responseObj = new JSONObject(body);
                    int numTransactions = responseObj.length();
                    for (int i = 0; i < numTransactions; i++) {
                        JSONObject transObj = responseObj.getJSONObject("" + i);
                        double amount = transObj.getDouble("amount");
                        int category = transObj.getInt("category");
                        int isDeposit = transObj.getInt("isDeposit");

                        if (isDeposit == 0) {
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
                            total += amount;
                        }
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    totalTitle.setText("Total Amount Spent: $" + df.format(total));
                    mBus.post(new SendCategoryTotalEvent());
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

    @Subscribe
    public void onGetMonthCategoryTotalEvent(GetMonthCategoryTotalEvent event) {
        Call<ResponseBody> getMonthCategoryTotalCall = mApiClient.getTransactionService().getMonthTransactions(event.getMonth(), event.getYear());
        getMonthCategoryTotalCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resetTotals();
                try {
                    String body = response.body().string();
                    JSONObject responseObj = new JSONObject(body);
                    int numTransactions = responseObj.length();
                    for (int i = 0; i < numTransactions; i++) {
                        JSONObject transObj = responseObj.getJSONObject("" + i);
                        double amount = transObj.getDouble("amount");
                        int category = transObj.getInt("category");
                        int isDeposit = transObj.getInt("isDeposit");

                        if (isDeposit == 0) {
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
                            total += amount;
                        }
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    totalTitle.setText("Total Amount Spent: $" + df.format(total));
                    mBus.post(new SendCategoryTotalEvent());
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

    @Subscribe
    public void onGetFullMonthTransactionEvent(GetFullMonthTransactionEvent event) {
        final int month = event.getMonth();
        final int year = event.getYear();

        Call<ResponseBody> getFullMonthTransactionCall = mApiClient.getTransactionService().getMonthTransactions(month, year);
        getFullMonthTransactionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                double depositTotal = 0.0;
                double withdrawTotal = 0.0;
                try {
                    String body = response.body().string();
                    JSONObject responseObj = new JSONObject(body);
                    int numTrans = responseObj.length();
                    for (int i = 0; i < numTrans; i++) {
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
                        mBus.post(new SendTransactionEvent(trans));
                    }
                    mBus.post(new SendAmountTotalEvent(withdrawTotal, depositTotal));
                    Collections.reverse(transactions);
                    transAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    @Subscribe
    public void onGetMonthTransactionEvent(GetMonthTransactionEvent event) {
        final int month = event.getMonth();
        final int year = event.getYear();
        final TextView monthTotal = event.monthTotal;
        final BarGraph monthGraph = event.monthGraph;
        Call<ResponseBody> getMonthTransactionCall = mApiClient.getTransactionService().getMonthTransactions(month, year);
        getMonthTransactionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                depositTotal = 0.0;
                withdrawTotal = 0.0;
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
                    mBus.post(new SendMonthBalanceEvent(withdrawTotal, depositTotal, month, year, monthTotal, monthGraph));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    @Subscribe
    public void onSendTransactionEvent(SendTransactionEvent event) {
        Transaction trans = event.getTransaction();
        transactions.add(trans);
        masterFilterList.add(trans);
        transAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onSendAmountTotalEvent(SendAmountTotalEvent event) {
        double withdraw = event.getWithdrawAmount();
        double deposit = event.getDepositAmount();
        double net = event.getNetAmount();
        DecimalFormat df = new DecimalFormat("0.00");
        totalBalanceView.setText("$" + df.format(net));
        withdrawTotalView.setText("$" + df.format(withdraw));
        depositTotalView.setText("$" + df.format(deposit));
    }

    @Subscribe
    public void onSendCategoryTotalEvent(SendCategoryTotalEvent event) {
        pg.removeSlices();
        pgSlices.clear();
        if (uncategorizedTotal > 0) {
            uncategorizedSlice = new PieSlice();
            uncategorizedSlice.setColor(Color.parseColor("#1188AA"));
            uncategorizedSlice.setValue((float) uncategorizedTotal);
            pg.addSlice(uncategorizedSlice);
            pgSlices.add(uncategorizedSlice);
        }
        if (foodTotal > 0) {
            foodSlice = new PieSlice();
            foodSlice.setColor(Color.parseColor("#FFBB33"));
            foodSlice.setValue((float) foodTotal);
            pg.addSlice(foodSlice);
            pgSlices.add(foodSlice);
        }
        if (gasTotal > 0) {
            gasSlice = new PieSlice();
            gasSlice.setColor(Color.parseColor("#AA66CC"));
            gasSlice.setValue((float) gasTotal);
            pg.addSlice(gasSlice);
            pgSlices.add(gasSlice);
        }
        if (clothesTotal > 0) {
            clothesSlice = new PieSlice();
            clothesSlice.setColor(Color.parseColor("#77DD11"));
            clothesSlice.setValue((float) clothesTotal);
            pg.addSlice(clothesSlice);
            pgSlices.add(clothesSlice);
        }
        if (techTotal > 0) {
            technologySlice = new PieSlice();
            technologySlice.setColor(Color.parseColor("#33AA55"));
            technologySlice.setValue((float) techTotal);
            pg.addSlice(technologySlice);
            pgSlices.add(technologySlice);
        }
        if (kitchenTotal > 0) {
            kitchenSlice = new PieSlice();
            kitchenSlice.setColor(Color.parseColor("#DD00FF"));
            kitchenSlice.setValue((float) kitchenTotal);
            pg.addSlice(kitchenSlice);
            pgSlices.add(kitchenSlice);
        }
        if (furnitureTotal > 0) {
            furnitureSlice = new PieSlice();
            furnitureSlice.setColor(Color.parseColor("#99CC00"));
            furnitureSlice.setValue((float) furnitureTotal);
            pg.addSlice(furnitureSlice);
            pgSlices.add(furnitureSlice);
        }

        categoryLabels.clear();
        catAdapter.notifyDataSetChanged();

        CategoryLabel noLabel = new CategoryLabel("#1188AA", "Uncategorized", (uncategorizedTotal / total) * 100, uncategorizedTotal);
        categoryLabels.add(noLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel foodLabel = new CategoryLabel("#FFBB33", "Food", (foodTotal / total) * 100, foodTotal);
        categoryLabels.add(foodLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel gasLabel = new CategoryLabel("#AA66CC", "Gas", (gasTotal / total) * 100, gasTotal);
        categoryLabels.add(gasLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel clothesLabel = new CategoryLabel("#77DD11", "Clothes", (clothesTotal / total) * 100, clothesTotal);
        categoryLabels.add(clothesLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel techLabel = new CategoryLabel("#33AA55", "Technology", (techTotal / total) * 100, techTotal);
        categoryLabels.add(techLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel kitchenLabel = new CategoryLabel("#DD00FF", "Kitchen Hardware", (kitchenTotal / total) * 100, kitchenTotal);
        categoryLabels.add(kitchenLabel);
        //catAdapter.notifyDataSetChanged();

        CategoryLabel furnitureLabel = new CategoryLabel("#99CC00", "Furniture", (furnitureTotal / total) * 100, furnitureTotal);
        categoryLabels.add(furnitureLabel);
        catAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onSendMonthBalanceEvent(SendMonthBalanceEvent event) {
        double withdrawTotal = event.getWithdrawTotal();
        double depositTotal = event.getDepositTotal();
        String month = util.getMonth(event.getMonth() - 1);
        int year = event.getYear();
        TextView monthTotal = event.monthTotal;
        BarGraph monthGraph = event.monthGraph;

        DecimalFormat df = new DecimalFormat("0.00");
        monthTotal.setText(month + " " + year + " Net Balance: $" + df.format(depositTotal - withdrawTotal));
        monthTotal.setTextColor(getResources().getColor(R.color.colorPrimary));
        monthTotal.setTextSize(20);

        ArrayList<Bar> monthPoints = new ArrayList<Bar>();
        Bar depositBar = new Bar();
        depositBar.setColor(Color.parseColor("#99CC00"));
        depositBar.setName("Deposit");
        depositBar.setValue((float) depositTotal);
        Bar withdrawBar = new Bar();
        withdrawBar.setColor(Color.parseColor("#FFBB33"));
        withdrawBar.setName("Withdraw");
        withdrawBar.setValue((float) withdrawTotal);
        monthPoints.add(depositBar);
        monthPoints.add(withdrawBar);

        monthGraph.setBars(monthPoints);
        monthGraph.setUnit("$");

    }

    @Subscribe
    public void onSendLabelClickEvent(SendLabelClickEvent event) {
        String clickedLabelName = event.getLabelName();
        if (clickedLabelName.equals("Uncategorized")) {
            uncategorizedSlice.setTitle("$" + uncategorizedTotal);

        }
    }

    public static class MonthStringUtil {
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("withdrawTotal", withdrawTotal);
        outState.putDouble("depositTotal", depositTotal);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        withdrawTotal = savedInstanceState.getDouble("withdrawTotal");
        depositTotal = savedInstanceState.getDouble("depositTotal");

        Date currentDate = new Date(new java.util.Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        final int month = cal.get(Calendar.MONTH);
        final int year = cal.get(Calendar.YEAR);

        mBus.register(this);
        registeredBus = true;

        mBus.post(new GetMonthTransactionEvent(month, year, lastMonthTotal, lastMonthGraph));
        mBus.post(new GetMonthTransactionEvent(month + 1, year, thisMonthTotal, thisMonthGraph));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currencyEditText.getWindowToken(), 0);

    }
}
