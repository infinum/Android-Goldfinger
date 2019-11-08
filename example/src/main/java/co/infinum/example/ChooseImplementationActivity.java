package co.infinum.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseImplementationActivity extends AppCompatActivity {

    private View exampleButton;
    private View rxExampleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setContentView(R.layout.activity_error);
            return;
        }

        SharedPrefs.init(this);
        setContentView(R.layout.activity_choose_implementation);
        initViews();
        initListeners();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void initListeners() {
        this.exampleButton.setOnClickListener(v -> navigateToSetPinActivity(false));
        this.rxExampleButton.setOnClickListener(v -> navigateToSetPinActivity(true));
    }

    private void initViews() {
        this.exampleButton = findViewById(R.id.exampleButton);
        this.rxExampleButton = findViewById(R.id.rxExampleButton);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void navigateToSetPinActivity(boolean isRxExample) {
        SharedPrefs.setRxExample(isRxExample);
        startActivity(new Intent(this, SetPinActivity.class));
    }
}
