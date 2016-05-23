package ypan01.financify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.sql.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yang on 5/20/2016.
 */
public class TransactionDetailActivity extends AppCompatActivity {
    private Transaction transaction;

    public enum CategoryIDs {
        Food,
        Gas,
        Clothes,
        Technology,
        KitchenHardware,
        Furniture
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_detail);

        final Spinner spinner = (Spinner)findViewById(R.id.withdraw_spinner);
        Button submitButton = (Button)findViewById(R.id.submit_cat);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                transaction = null;
            }
            else {
                int transactionId = extras.getInt("trans_id");
                int isDeposit = extras.getInt("trans_isDeposit");
                double amount = extras.getDouble("trans_amount");
                String dateStr = extras.getString("trans_date");
                Date date = Date.valueOf(dateStr);
                int category = extras.getInt("trans_cat");
                transaction = new Transaction(transactionId, isDeposit, amount, date, category);
            }
        }
        else {
            int transactionId = (int) savedInstanceState.getSerializable("trans_id");
            int isDeposit = (int) savedInstanceState.getSerializable("trans_isDeposit");
            double amount = (double) savedInstanceState.getSerializable("trans_amount");
            String dateStr = (String) savedInstanceState.getSerializable("trans_date");
            Date date = Date.valueOf(dateStr);
            int category = (int) savedInstanceState.getSerializable("trans_cat");
            transaction = new Transaction(transactionId, isDeposit, amount, date, category);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCat = spinner.getSelectedItem().toString();
                int selectedCatId = getCategoryId(selectedCat);

                Handler handler = new Handler(Looper.getMainLooper());
                String baseURL = getResources().getString(R.string.api_url);
                Retrofit client = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();
                TransactionService service = client.create(TransactionService.class);

                Call<ResponseBody> updateCategoryCall = service.updateCategory(transaction.transactionId, selectedCatId);
                updateCategoryCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //Intent openMain = new Intent(getApplicationContext(), MainActivity.class);
                        //getApplicationContext().startActivity(openMain);
                        try {
                            String body = response.body().string();
                            JSONObject obj = new JSONObject(body);
                            onBackPressed();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });

            }
        });

    }

    protected int getCategoryId(String categoryName) {
        if (categoryName.equals("Food")) {
            return 1;
        } else if (categoryName.equals("Gas")) {
            return 2;
        } else if (categoryName.equals("Clothes")) {
            return 3;
        } else if (categoryName.equals("Technology")) {
            return 4;
        } else if (categoryName.equals("Kitchen Hardware")) {
            return 5;
        } else if (categoryName.equals("Furniture")) {
            return 6;
        }
        return 0;
    }
}
