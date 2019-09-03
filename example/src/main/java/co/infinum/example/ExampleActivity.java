package co.infinum.example;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import co.infinum.goldfinger.Goldfinger;

public class ExampleActivity extends AppCompatActivity {

    private static final String KEY_NAME = "key";

    private View authenticateButton;
    private View decryptButton;
    private View encryptButton;
    private View cancelButton;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        fetchViews();
        initListeners();
        goldfinger = new Goldfinger.Builder(this).logEnabled(BuildConfig.DEBUG).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (goldfinger.canAuthenticate()) {
            authenticateButton.setEnabled(true);
        } else {
            authenticateButton.setEnabled(false);
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
        cancelButton.setEnabled(true);
        Goldfinger.Params params = baseParams().build();
        goldfinger.authenticate(params, new Goldfinger.Callback() {
            @Override
            public void onError(@NonNull Exception e) {
                onGoldfingerError();
            }

            @Override
            public void onResult(@NonNull Goldfinger.Result result) {
                onGoldfingerResult(result);
            }
        });
    }

    private Goldfinger.Params.Builder baseParams() {
        return new Goldfinger.Params.Builder(this)
            .description("Description")
            .subtitle("Subtitle")
            .title("Title")
            .negativeButtonText("Cancel");
    }

    private void decryptEncryptedValue() {
        cancelButton.setEnabled(true);
        Goldfinger.Params params = baseParams().decrypt(KEY_NAME, encryptedValue).build();
        goldfinger.authenticate(params, new Goldfinger.Callback() {
            @Override
            public void onError(@NonNull Exception e) {
                onGoldfingerError();
            }

            @Override
            public void onResult(@NonNull Goldfinger.Result result) {
                onGoldfingerResult(result);
            }
        });
    }

    private void encryptSecretValue() {
        cancelButton.setEnabled(true);
        Goldfinger.Params params = baseParams().encrypt(KEY_NAME, secretInputView.getText().toString()).build();
        goldfinger.authenticate(params, new Goldfinger.Callback() {
            @Override
            public void onError(@NonNull Exception e) {
                onGoldfingerError();
            }

            @Override
            public void onResult(@NonNull Goldfinger.Result result) {
                onGoldfingerResult(result);
                encryptedValue = result.value();
                decryptButton.setEnabled(true);
            }
        });
    }

    private void fetchViews() {
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        authenticateButton = findViewById(R.id.authenticateButton);
        secretInputView = findViewById(R.id.secretInputView);
        statusView = findViewById(R.id.statusView);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void initListeners() {
        encryptButton.setOnClickListener(v -> {
            resetStatusText();
            encryptSecretValue();
        });

        decryptButton.setOnClickListener(v -> {
            resetStatusText();
            decryptEncryptedValue();
        });

        authenticateButton.setOnClickListener(v -> {
            resetStatusText();
            authenticateUserFingerprint();
        });

        cancelButton.setOnClickListener(v -> {
            cancelButton.setEnabled(false);
            goldfinger.cancel();
        });

        secretInputView.addTextChangedListener(onTextChangedListener);
    }

    private void onGoldfingerError() {
        cancelButton.setEnabled(false);
        statusView.setTextColor(ContextCompat.getColor(this, R.color.error));
        statusView.setText(getString(R.string.error));
    }

    private void onGoldfingerResult(Goldfinger.Result result) {
        statusView.setText(getString(R.string.status, result.type(), result.reason(), result.value(), result.message()));
        Goldfinger.Type type = result.type();
        if (type == Goldfinger.Type.SUCCESS) {
            cancelButton.setEnabled(false);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.ok));
        } else if (type == Goldfinger.Type.INFO) {
            statusView.setTextColor(ContextCompat.getColor(this, R.color.info));
        } else if (type == Goldfinger.Type.ERROR) {
            cancelButton.setEnabled(false);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.error));
        }
    }

    private void resetStatusText() {
        statusView.setTextColor(ContextCompat.getColor(this, R.color.textRegular));
        statusView.setText(getString(R.string.authenticate_user));
    }
}
