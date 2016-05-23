package ypan01.financify;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.sql.Date;

/**
 * Created by Yang on 5/14/2016.
 */
public interface TransactionService {

    @GET("transactions")
    Call<ResponseBody> getTransactions();

    @GET("transMonth/{month}/{year}")
    Call<ResponseBody> getMonthTransactions(@Path("month") int month, @Path("year") int year);

    @FormUrlEncoded
    @POST("transaction")
    Call<ResponseBody> createTransaction(@Field("isDeposit") int type, @Field("amount") double amt, @Field("date") Date date);

    @FormUrlEncoded
    @PUT("transUpdate/{id}")
    Call<ResponseBody> updateCategory(@Path("id") int id, @Field("category") int category);

}
