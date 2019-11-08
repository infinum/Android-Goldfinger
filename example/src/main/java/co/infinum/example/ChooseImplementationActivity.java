package co.infinum.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseImplementationActivity extends AppCompatActivity {

    private View pinLoginExampleButton;
    private View pinLoginRxExampleButton;
    private View paymentExampleButton;
    private View paymentRxExampleButton;

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
        this.pinLoginExampleButton.setOnClickListener(v -> navigateToSetPinActivity(false));
        this.pinLoginRxExampleButton.setOnClickListener(v -> navigateToSetPinActivity(true));
        this.paymentExampleButton.setOnClickListener(v -> navigateToPaymentActivity(false));
        this.paymentRxExampleButton.setOnClickListener(v -> navigateToPaymentActivity(true));
    }

    private void initViews() {
        this.pinLoginExampleButton = findViewById(R.id.pinLoginExampleButton);
        this.pinLoginRxExampleButton = findViewById(R.id.pinLoginRxExampleButton);
        this.paymentExampleButton = findViewById(R.id.paymentExampleButton);
        this.paymentRxExampleButton = findViewById(R.id.paymentRxExampleButton);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void navigateToPaymentActivity(boolean isRxExample) {
        Class<?> cls = isRxExample ? RxPaymentActivity.class : PaymentActivity.class;
        startActivity(new Intent(this, cls));
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void navigateToSetPinActivity(boolean isRxExample) {
        SharedPrefs.setRxExample(isRxExample);
        startActivity(new Intent(this, SetPinActivity.class));
        finish();
    }
}
