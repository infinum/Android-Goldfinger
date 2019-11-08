package co.infinum.example;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import co.infinum.goldfinger.Goldfinger;

public abstract class BasePaymentActivity extends AppCompatActivity {

    protected abstract void authenticateUser();
    protected abstract void initGoldfinger();

    private View paymentButton;
    private View userAuthenticatedView;

    protected Goldfinger.PromptParams buildPromptParams() {
        return new Goldfinger.PromptParams.Builder(this)
            .title("Payment")
            .description("Authenticate Fingerprint to proceed with payment")
            /* Device credentials can be used here */
            //            .deviceCredentialsAllowed(true)
            .negativeButtonText("Cancel")
            .build();
    }

    protected void handleGoldfingerResult(Goldfinger.Result result) {
        if (result.type() == Goldfinger.Type.SUCCESS) {
            userAuthenticatedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initGoldfinger();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userAuthenticatedView.setVisibility(View.GONE);
    }

    private void initListeners() {
        paymentButton.setOnClickListener(v -> {
            userAuthenticatedView.setVisibility(View.GONE);
            authenticateUser();
        });
    }

    private void initViews() {
        paymentButton = findViewById(R.id.paymentButton);
        userAuthenticatedView = findViewById(R.id.userAuthenticatedView);
    }
}
