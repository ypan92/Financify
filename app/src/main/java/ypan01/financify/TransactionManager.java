package ypan01.financify;


import android.content.Context;

import com.squareup.otto.Bus;

public class TransactionManager {
    private Context mContext;
    private Bus mBus;
    private ApiClient sApiClient;

    public TransactionManager(Context context, Bus bus) {
        mContext = context;
        mBus = bus;
        sApiClient = ApiClient.getInstance();
    }
}
