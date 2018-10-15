package com.example.andrey.applicationb.main;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.andrey.applicationb.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "mLog";
    private MainContract.Presenter mainPresenter;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            if (getIntent() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.url_extra), getIntent().getStringExtra(getString(R.string.url_extra)));
                bundle.putInt(getString(R.string.status_extra), getIntent().getIntExtra(getString(R.string.status_extra), -1));
                mainFragment.setArguments(bundle);
                Log.d(TAG, "url: " + getIntent().getStringExtra(getString(R.string.url_extra)));
                getIntent().removeExtra(getString(R.string.url_extra));
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, mainFragment)
                    .commit();
        }

        mainPresenter = new MainPresenter(mainFragment);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
      //  mainFragment.updateImage(getIntent().getStringExtra(getString(R.string.url_extra)));
        super.onRestart();
    }
}
