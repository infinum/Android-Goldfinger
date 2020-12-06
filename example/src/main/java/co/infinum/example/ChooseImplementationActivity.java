package co.infinum.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import co.infinum.goldfinger.Goldfinger;

public class ChooseImplementationActivity extends AppCompatActivity {

    private TextView strongAuthenticator;
    private TextView weakAuthenticator;
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
        initAuthenticatorsData();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void initListeners() {
        this.pinLoginExampleButton.setOnClickListener(v -> navigateToSetPinActivity(false));
        this.pinLoginRxExampleButton.setOnClickListener(v -> navigateToSetPinActivity(true));
        this.paymentExampleButton.setOnClickListener(v -> navigateToPaymentActivity(false));
        this.paymentRxExampleButton.setOnClickListener(v -> navigateToPaymentActivity(true));
    }

    private void initViews() {
        this.strongAuthenticator = findViewById(R.id.strongAuthenticator);
        this.weakAuthenticator = findViewById(R.id.weakAuthenticator);
        this.pinLoginExampleButton = findViewById(R.id.pinLoginExampleButton);
        this.pinLoginRxExampleButton = findViewById(R.id.pinLoginRxExampleButton);
        this.paymentExampleButton = findViewById(R.id.paymentExampleButton);
        this.paymentRxExampleButton = findViewById(R.id.paymentRxExampleButton);
    }

    private void initAuthenticatorsData() {
        Goldfinger goldfinger = new Goldfinger.Builder(this).build();

        strongAuthenticator.setText(constructBiometricsInfoString(goldfinger, BiometricManager.Authenticators.BIOMETRIC_STRONG));
        weakAuthenticator.setText(constructBiometricsInfoString(goldfinger, BiometricManager.Authenticators.BIOMETRIC_WEAK));
    }

    private String constructBiometricsInfoString(Goldfinger goldfinger, int authenticator) {
        boolean hasAvailableAuthentication = goldfinger.canAuthenticate(authenticator);
        boolean hasBiometricEnrolled = goldfinger.hasEnrolledFingerprint(authenticator);
        boolean hasBiometricHardware = goldfinger.hasFingerprintHardware(authenticator);

        StringBuilder stringBuilder = new StringBuilder(
            String.format("Authenticator %s available", hasAvailableAuthentication ? "is" : "NOT")
        );

        if (hasAvailableAuthentication) {
            stringBuilder.append(
                String.format("\n - %s \n - %s",
                    String.format("At least one biometric %s enrolled", hasBiometricEnrolled ? "is" : "NOT"),
                    String.format("Biometrics hardware %s detected", hasBiometricHardware ? "is" : "NOT")
                )
            );
        }

        return stringBuilder.toString();
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
