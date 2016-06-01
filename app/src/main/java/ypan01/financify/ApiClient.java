package ypan01.financify;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String baseURL =
            "http://default-environment.eyqmmrug4y.us-east-1.elasticbeanstalk.com/iconmerce-api/";

    private static volatile ApiClient instance;

    private TransactionService mTransactionService;

    private ApiClient() {
        init();
    }

    public static ApiClient getInstance() {
        ApiClient localInstance = instance;
        if (localInstance == null) {
            synchronized (ApiClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ApiClient();
                }
            }
        }
        return localInstance;
    }

    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        mTransactionService = retrofit.create(TransactionService.class);
    }

    public TransactionService getTransactionService() {
        return mTransactionService;
    }
}
