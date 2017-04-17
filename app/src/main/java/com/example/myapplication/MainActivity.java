package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private SimpleService.GitHub githubService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.coom") // WRONG URL
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        // Create an instance of our GitHub API interface.
        githubService = retrofit.create(SimpleService.GitHub.class);

        // Wire buttons
        findViewById(R.id.btn_retrofit_exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofitServiceException();
            }
        });

        findViewById(R.id.btn_rxjava_exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plainRxJavaException();
            }
        });
    }

    /**
     * Service will pass {@link java.net.UnknownHostException} to onError because of wrong URL
     */
    private void retrofitServiceException() {
        githubService.contributors("square", "retrofit")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SimpleService.Contributor>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Force java.lang.IndexOutOfBoundsException
                        // App will crash, exception will be swollen (not visible in Logcat)
                        int[] ints = new int[2];
                        int intValue = ints[10];
                    }

                    @Override
                    public void onNext(List<SimpleService.Contributor> contributors) {
                    }
                });
    }


    private void plainRxJavaException() {
        Observable.error(new RuntimeException())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // Force java.lang.IndexOutOfBoundsException
                        // App will crash, exception is visible in Logcat
                        int[] ints = new int[2];
                        int intValue = ints[10];
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }
}
