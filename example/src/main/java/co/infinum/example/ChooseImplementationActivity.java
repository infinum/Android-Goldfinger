package co.infinum.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import co.infinum.goldfinger.Goldfinger;

public class ChooseImplementationActivity extends AppCompatActivity {

    private CheckBox strongCheckBox;
    private TextView strongAuthenticator;
    private CheckBox weakCheckBox;
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

    private void initListeners() {
        this.pinLoginExampleButton.setOnClickListener(v -> navigateToSetPinActivity(false));
        this.pinLoginRxExampleButton.setOnClickListener(v -> navigateToSetPinActivity(true));
        this.paymentExampleButton.setOnClickListener(v -> navigateToPaymentActivity(false));
        this.paymentRxExampleButton.setOnClickListener(v -> navigateToPaymentActivity(true));
        this.strongCheckBox.setOnCheckedChangeListener((compoundButton, b) -> updateAuthenticators());
        this.weakCheckBox.setOnCheckedChangeListener((compoundButton, b) -> updateAuthenticators());
    }

    private void updateAuthenticators() {
        int authenticators = 0;
        if (strongCheckBox.isChecked()) {
            authenticators |= BiometricManager.Authenticators.BIOMETRIC_STRONG;
        }
        if (weakCheckBox.isChecked()) {
            authenticators |= BiometricManager.Authenticators.BIOMETRIC_WEAK;
        }
        SharedPrefs.setAuthenticators(authenticators);
    }

    private void initViews() {
        this.strongCheckBox = findViewById(R.id.strongCheckBox);
        this.strongAuthenticator = findViewById(R.id.strongAuthenticator);
        this.weakCheckBox = findViewById(R.id.weakCheckBox);
        this.weakAuthenticator = findViewById(R.id.weakAuthenticator);
        this.pinLoginExampleButton = findViewById(R.id.pinLoginExampleButton);
        this.pinLoginRxExampleButton = findViewById(R.id.pinLoginRxExampleButton);
        this.paymentExampleButton = findViewById(R.id.paymentExampleButton);
        this.paymentRxExampleButton = findViewById(R.id.paymentRxExampleButton);
    }

    private void initAuthenticatorsData() {
        Goldfinger goldfinger = new Goldfinger.Builder(this).build();

        strongCheckBox.setEnabled(goldfinger.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG));
        strongAuthenticator.setText(constructBiometricsInfoString(goldfinger, BiometricManager.Authenticators.BIOMETRIC_STRONG));
        weakCheckBox.setEnabled(goldfinger.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK));
        weakAuthenticator.setText(constructBiometricsInfoString(goldfinger, BiometricManager.Authenticators.BIOMETRIC_WEAK));
    }

    private String constructBiometricsInfoString(Goldfinger goldfinger, int authenticator) {
        boolean hasAvailableAuthentication = goldfinger.canAuthenticate(authenticator);
        boolean hasBiometricEnrolled = goldfinger.hasEnrolledFingerprint(authenticator);
        boolean hasBiometricHardware = goldfinger.hasFingerprintHardware(authenticator);

        StringBuilder stringBuilder = new StringBuilder(
            String.format("authenticator %s available", hasAvailableAuthentication ? "is" : "NOT")
        );

        if (hasAvailableAuthentication) {
            stringBuilder.append(
                String.format("\n- %s \n- %s",
                    String.format("biometrics hardware %s detected", hasBiometricHardware ? "is" : "NOT"),
                    String.format("at least one biometric %s enrolled", hasBiometricEnrolled ? "is" : "NOT")
                )
            );
        }

        return stringBuilder.toString();
    }

    private void navigateToPaymentActivity(boolean isRxExample) {
        Class<?> cls = isRxExample ? RxPaymentActivity.class : PaymentActivity.class;
        startActivity(new Intent(this, cls));
    }

    private void navigateToSetPinActivity(boolean isRxExample) {
        SharedPrefs.setRxExample(isRxExample);
        startActivity(new Intent(this, SetPinActivity.class));
        finish();
    }
}
