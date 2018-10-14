package com.example.andrey.applicationb.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.andrey.applicationb.R;
import com.example.andrey.applicationb.util.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements MainContract.View {

    private MainContract.Presenter mainPresenter;
    private final String TAG = "mLog";
    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    RxPermissions rxPermissions;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imageView)
    ImageView imageView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mainPresenter = presenter;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void init() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        rxPermissions = new RxPermissions(this);

        if (getArguments() != null) {
            String imageUrl = getArguments().getString(getString(R.string.url_extra), "");
            if (!TextUtils.isEmpty(imageUrl))
            {
                loadImageToImageView(imageUrl);
            }
            else {
                Log.d(TAG, "application should be closed");
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        mainPresenter.clear();
        super.onDestroy();
    }

    @Override//this method is not using now because activity creates each time
    public void updateImage(String url) {
        if (!TextUtils.isEmpty(url))
            loadImageToImageView(url);
        else
            Log.d(TAG, "do nothing");
    }

    public void saveToDatabaseOfAppA(String imageUrl, int status){
        Intent intent = new Intent(getString(R.string.receiver_action));
        intent.putExtra(getString(R.string.url_extra), imageUrl);
        intent.putExtra(getString(R.string.status_extra), status);
        intent.putExtra(getString(R.string.last_opened_extra), Calendar.getInstance().getTime().getTime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Error while sending intent");
            e.printStackTrace();
        }
    }

    public void saveToExternalstorage(String imageUrl){
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        mainPresenter.saveToExternalStorage(((BitmapDrawable) imageView.getDrawable()).getBitmap(),
                                StringUtil.getFileNameFromUrl(imageUrl));
                    } else {
                        Log.d(TAG, "permission denied");
                    }
                });
    }

    public void loadImageToImageView(String imageUrl) {
        Picasso.get().load(imageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
                progressBar.setVisibility(View.INVISIBLE);
                saveToDatabaseOfAppA(imageUrl, 1);
               // saveToExternalstorage(imageUrl); //todo use in another case
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError" + " " + e);
                progressBar.setVisibility(View.INVISIBLE);
                saveToDatabaseOfAppA(imageUrl, 2);
                showMessage(R.string.error_happened_while_loading_image);
            }
        });
    }

    @Override
    public void showMessage(int resourceId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setMessage(resourceId)
                .setPositiveButton(R.string.ok, (dialog, wich) -> dialog.dismiss())
                .show();
    }

    @Override
    public void showLoadingState(boolean flag) {
        if (flag)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }
}
