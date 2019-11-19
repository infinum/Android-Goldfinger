package co.infinum.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import co.infinum.goldfinger.Goldfinger;

@RequiresApi(Build.VERSION_CODES.M)
public abstract class BaseLoginActivity extends AppCompatActivity {

    protected abstract void decryptFingerprintPin(String encryptedPin);
    protected abstract void initGoldfinger();

    private EditText pinInputView;
    private View pinLoginButton;
    private View fingerprintLoginButton;
    private TextView errorView;
    private View resetButton;
    private MockLoginService loginService = new MockLoginService();
    private MockLoginService.Callback callback = new MockLoginService.Callback() {
        @Override
        public void onFailure() {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Invalid PIN");
        }

        @Override
        public void onSuccess() {
            navigateToSettings();
        }
    };

    protected Goldfinger.PromptParams buildPromptParams() {
        return new Goldfinger.PromptParams.Builder(this)
            .title("Login")
            .description("Confirm Fingerprint to Login")
            .negativeButtonText("Cancel")
            .build();
    }

    protected void handleGoldfingerError() {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText("Fingerprint exception - check log.");
    }

    protected void handleGoldfingerResult(@NonNull Goldfinger.Result result) {
        if (result.type() == Goldfinger.Type.SUCCESS) {
            loginService.login(result.value(), callback);
        } else if (result.type() == Goldfinger.Type.ERROR) {
            errorView.setVisibility(View.VISIBLE);
            String formattedResult = String.format("%s - %s", result.type().toString(), result.reason().toString());
            errorView.setText(formattedResult);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initGoldfinger();
        initViews();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        pinLoginButton.setEnabled(pinInputView.getText().toString().length() == 4);
        fingerprintLoginButton.setEnabled(SharedPrefs.getFingerprintPin() != null);
    }

    @SuppressLint("SetTextI18n")
    private void initListeners() {
        pinInputView.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                errorView.setVisibility(View.GONE);
                pinLoginButton.setEnabled(text.length() == 4);
            }
        });

        pinLoginButton.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);

            loginService.login(pinInputView.getText().toString(), callback);
        });

        fingerprintLoginButton.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            String encryptedPin = SharedPrefs.getFingerprintPin();
            decryptFingerprintPin(encryptedPin);
        });

        resetButton.setOnClickListener(v -> {
            SharedPrefs.clear();
            startActivity(new Intent(this, ChooseImplementationActivity.class));
            finish();
        });
    }

    private void initViews() {
        pinInputView = findViewById(R.id.pinInputView);
        pinLoginButton = findViewById(R.id.pinLoginButton);
        fingerprintLoginButton = findViewById(R.id.fingerprintLoginButton);
        errorView = findViewById(R.id.errorView);
        resetButton = findViewById(R.id.resetButton);
    }

    private void navigateToSettings() {
        Class<?> cls = SharedPrefs.isRxExample() ? RxSettingsActivity.class : SettingsActivity.class;
        startActivity(new Intent(BaseLoginActivity.this, cls));
    }
}
