package com.example.andrey.applicationb.main;

import android.graphics.Bitmap;

public interface MainContract {

    interface View{
        void setPresenter(MainContract.Presenter presenter);
        void updateImage(String url);
        void showMessage(int resourceId);
        void showLoadingState(boolean flag);
    }

    interface Presenter{
        void saveToExternalStorage(Bitmap bitmap, String fileName);
        void clear();
    }
}
