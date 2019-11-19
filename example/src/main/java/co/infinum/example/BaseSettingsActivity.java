package co.infinum.example;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import co.infinum.goldfinger.Goldfinger;

@RequiresApi(Build.VERSION_CODES.M)
public abstract class BaseSettingsActivity extends AppCompatActivity {

    protected abstract void initGoldfinger();
    protected abstract void encryptFingerprintPin(String pin);

    private TextView currentPinView;
    private EditText pinInputView;
    private View changePinButton;
    private CompoundButton fingerprintSwitchView;
    private View logoutButton;

    protected Goldfinger.PromptParams buildPromptParams() {
        return new Goldfinger.PromptParams.Builder(this)
            .title("Settings")
            .description("Confirm Fingerprint to enable Fingerprint Login")
            .negativeButtonText("Cancel")
            .build();
    }

    protected void handleGoldfingerError() {
        fingerprintSwitchView.setChecked(false);
    }

    protected void handleGoldfingerResult(Goldfinger.Result result) {
        if (result.type() == Goldfinger.Type.SUCCESS) {
            SharedPrefs.setFingerprintPin(result.value());
        } else if (result.type() == Goldfinger.Type.ERROR) {
            fingerprintSwitchView.setChecked(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initGoldfinger();
        initViews();
        initListeners();
    }

    private void activateFingerprint() {
        String pin = SharedPrefs.getPin();
        if (pin == null) {
            return;
        }

        encryptFingerprintPin(pin);
    }

    private void initListeners() {
        pinInputView.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                changePinButton.setEnabled(text.length() == 4);
            }
        });

        logoutButton.setOnClickListener(v -> finish());

        fingerprintSwitchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                activateFingerprint();
            } else {
                SharedPrefs.clearFingerprintPin();
            }
        });

        changePinButton.setOnClickListener(v -> {
            String pin = pinInputView.getText().toString();
            SharedPrefs.setPin(pin);

            fingerprintSwitchView.setChecked(false);
            currentPinView.setText("Current PIN is = " + pin);
        });
    }

    private void initViews() {
        currentPinView = findViewById(R.id.currentPinView);
        pinInputView = findViewById(R.id.pinInputView);
        changePinButton = findViewById(R.id.changePinButton);
        fingerprintSwitchView = findViewById(R.id.fingerprintSwitchView);
        logoutButton = findViewById(R.id.logoutButton);

        currentPinView.setText("Current PIN is = " + SharedPrefs.getPin());
        fingerprintSwitchView.setChecked(SharedPrefs.getFingerprintPin() != null);
        changePinButton.setEnabled(false);
    }
}
