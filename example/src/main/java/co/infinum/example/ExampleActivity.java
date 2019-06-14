package co.infinum.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerCallback;
import co.infinum.goldfinger.GoldfingerParams;

public class ExampleActivity extends AppCompatActivity {

    private static final String EXAMPLE_KEY = "key";

    private View authenticateButton;
    private View decryptButton;
    private View encryptButton;

    private String encryptedValue;
    private Goldfinger goldfinger;
    private OnTextChangedListener onTextChangedListener = new OnTextChangedListener() {
        @Override
        void onTextChanged(String text) {
            encryptButton.setEnabled(!text.isEmpty());
        }
    };
    private EditText secretInputView;
    private TextView statusView;
    private GoldfingerCallback callback = new GoldfingerCallback() {
        @Override
        public void onError(Error error) {
            onErrorResult(error);
        }

        @Override
        public void onFail() {
            Log.d("Goldfinger", "onFail");
        }

        @Override
        public void onSuccess(Goldfinger.Result result) {
            switch (result.reason()) {
                case ENCRYPTION:
                    Log.d("Goldfinger", "Data encrypted.");
                    encryptedValue = result.value();
                    decryptButton.setEnabled(true);
                    break;
                case DECRYPTION:
                    Log.d("Goldfinger", "Data decrypted.");
                    break;
                case AUTHENTICATION:
                    Log.d("Goldfinger", "User authenticated");
                    break;
            }
            onSuccessResult(result.value());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        authenticateButton = findViewById(R.id.authenticateButton);
        secretInputView = findViewById(R.id.secretInputView);
        statusView = findViewById(R.id.statusView);

        goldfinger = new Goldfinger.Builder(this).logEnabled(BuildConfig.DEBUG).build();

        secretInputView.addTextChangedListener(onTextChangedListener);
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        authenticateButton.setEnabled(goldfinger.hasFingerprintHardware());
        if (!goldfinger.hasFingerprintHardware()) {
            statusView.setText(getString(R.string.fingerprint_not_available));
            statusView.setTextColor(ContextCompat.getColor(this, R.color.error));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        goldfinger.cancel();
    }

    private void authenticateUserFingerprint() {
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .title("Title")
            .description("Description")
            .negativeButtonText("Cancel")
            .subtitle("Subtitle")
            .build();
        goldfinger.authenticate(params, callback);
    }

    private void decryptEncryptedValue() {
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .cryptographyData(EXAMPLE_KEY, encryptedValue)
            .title("Title")
            .description("Description")
            .negativeButtonText("Cancel")
            .subtitle("Subtitle")
            .build();
        goldfinger.decrypt(params, callback);
    }

    private void encryptSecretValue() {
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .title("Title")
            .description("Description")
            .negativeButtonText("Cancel")
            .subtitle("Subtitle")
            .cryptographyData(EXAMPLE_KEY, secretInputView.getText().toString())
            .build();
        goldfinger.encrypt(params, callback);
    }

    private void initListeners() {
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStatusText();
                encryptSecretValue();
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStatusText();
                decryptEncryptedValue();
            }
        });

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStatusText();
                authenticateUserFingerprint();
            }
        });
    }

    private void onErrorResult(Error error) {
        onResult("onError", error.toString());
        if (error.shouldInvalidateFingerprint()) {
            statusView.setTextColor(ContextCompat.getColor(this, R.color.error));
        } else {
            statusView.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private void onResult(String methodName, String value) {
        statusView.setText(String.format(Locale.US, "%s - [%s]", methodName, value));
    }

    private void onSuccessResult(String value) {
        onResult("onSuccess", value);
        statusView.setTextColor(ContextCompat.getColor(this, R.color.ok));
    }

    private void resetStatusText() {
        statusView.setTextColor(ContextCompat.getColor(this, R.color.textRegular));
        statusView.setText(getString(R.string.authenticate_user));
    }
}
