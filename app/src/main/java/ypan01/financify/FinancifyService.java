package ypan01.financify;

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

    @FormUrlEncoded
    @POST("transaction")
    Call<ResponseBody> createTransaction(@Field("isDeposit") int type, @Field("amount") double amt);


}
