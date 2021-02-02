package com.hfr.market.connect;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * 使用okhttp访问网络
 */
public class ConnectUtils {

    private static final String TAG = "ConnectUtils";
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private FileUtils fileUtils;

    public ConnectUtils(Context context) {
        fileUtils = new FileUtils();
        fileUtils.init(context);
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 定义了update的操作，使用RxAndroid的操作思想
     *
     * @param url
     * @return
     */
    public rx.Observable<String> updateData(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //访问指定服务器地址
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                //得到服务器返回的具体数据 // TODO: 2020/3/12  获取一下默认的假数据
//                                subscriber.onNext(response.body().string());
//                                String jsonString = FileUtils.readFileData(context);
                                //String fileName = "assets/" + "data.json";
//                                String fileName = "/data.txt";
//                                String jsonString = fileUtils.readSDFile(fileName);
                                String jsonString = "[\n" +
                                        "  {\n" +
                                        "    \"price\": 6,\n" +
                                        "    \"name\": \"Potato\",\n" +
                                        "    \"type\": \"V\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"price\": 9,\n" +
                                        "    \"name\": \"Cucumber\",\n" +
                                        "    \"type\": \"V\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"price\": 8,\n" +
                                        "    \"name\": \"Pumpkin\",\n" +
                                        "    \"type\": \"V\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"price\": 10,\n" +
                                        "    \"name\": \"apple\",\n" +
                                        "    \"type\": \"F\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"price\": 5,\n" +
                                        "    \"name\": \"banana\",\n" +
                                        "    \"type\": \"F\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"price\": 6,\n" +
                                        "    \"name\": \"Orange\",\n" +
                                        "    \"type\": \"F\"\n" +
                                        "  }\n" +
                                        "]";
                                Log.i(TAG,"Thread = " + Thread.currentThread().getName());
                                subscriber.onNext(jsonString);
                            }
                            subscriber.onCompleted();
                        }
                    });
                }
            }
        });
    }

    /**
     * 定义了update的操作，使用RxAndroid的操作思想
     *
     * @param url
     * @return
     */
    public rx.Observable<String> postData(final String url, final String jsonData) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    RequestBody requestBody = RequestBody.create(JSON, jsonData);
                    //访问指定服务器地址
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                subscriber.onNext("上传成功");
                            }
                            String fileName =  "/data.txt";
                            Log.i(TAG,"jsondata = " + jsonData);
                            fileUtils.writeSDFile(fileName,jsonData);
                            subscriber.onCompleted();
                        }
                    });
                }
            }
        });
    }
}
