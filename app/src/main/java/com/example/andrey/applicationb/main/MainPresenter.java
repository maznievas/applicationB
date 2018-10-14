package com.example.andrey.applicationb.main;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AndroidException;
import android.util.Log;

import com.example.andrey.applicationb.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    MainContract.View mainView;
    private CompositeDisposable compositeDisposable;
    private final String TAG = "mLog";

    public MainPresenter(MainContract.View view) {
        mainView = view;
        mainView.setPresenter(this);

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void saveToExternalStorage(Bitmap bitmap, String fname) {
        Flowable.just(Environment.getExternalStorageDirectory().toString())
                .map(root -> {
                    return new File(root + Const.IMAGE_PATH);
                })
                .map(destDir -> {
                    if (!destDir.exists())
                        destDir.mkdirs();
                    return destDir;
                })
                .map(destDir -> {
                    return new File(destDir, fname);
                })
                .map(file -> {
                    if (file.exists())
                        file.delete();
                    return file;
                })
                .flatMapCompletable(file -> Completable.fromAction(() -> {
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(__ -> mainView.showLoadingState(true))
                .doAfterTerminate(() -> mainView.showLoadingState(false))
                .subscribe(() -> {
                    Log.d(TAG, "File successfully saved");
                }, throwable -> {
                    Log.e(TAG, "File saving exception");
                    throwable.printStackTrace();
                });

//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/BIGDIG/test/B");
//        if (!myDir.exists()) {
//            boolean mkdirsResult = myDir.mkdirs();
//            Log.d(TAG, "directory created: " + mkdirsResult);
//        }
//        File file = new File(myDir, fname);
//        if (file.exists())
//            file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void clear() {
        compositeDisposable.clear();
    }
}
