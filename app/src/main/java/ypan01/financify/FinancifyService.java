package ypan01.financify;

import java.sql.Date;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Yang on 5/14/2016.
 */
public interface FinancifyService {

    @GET("transactions")
    Call<ResponseBody> getTransactions();

    @GET("transMonth")
    Call<ResponseBody> getMonthTransactions(@Field("month") int month, @Field("year") int year);

    @FormUrlEncoded
    @POST("transaction")
    Call<ResponseBody> createTransaction(@Field("isDeposit") int type, @Field("amount") double amt, @Field("date") Date date);


}
