package toi.com.trivia.api;


import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetroFit {

    private static final String TAG = "ApiRetroFit";

    private APIService apiService;


    // Test server
    private static final String API_BASE_URL = "http://toi.techletsolutions.com/api/v1/";
    //production server
    //private static final String API_BASE_URL = "http://trivia.indiatimes.com/api/v1/";


    // Main server   private static final String API_REGISTER_URL = "http://toi.techletsolutions.com/api/v1/";
    //private static final String API_BASE_URL = "http://46.166.160.13";


    public ApiRetroFit() {
        Log.d(TAG, "ApiRetroFit");
        OkHttpClient okClient = new OkHttpClient();

        //code to increase timeout
      /*  final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();*/
      /*  Proxy proxyTest=new Proxy();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxyTest);
        OkHttpClient okClient = builder.build();
*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);
    }

    public APIService getApiService() {
        Log.d(TAG, "getApiService");
        return apiService;
    }

}
