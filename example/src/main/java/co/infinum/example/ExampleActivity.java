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

    private static final String KEY_NAME = "Example";

    private View authenticateButton;
    private View decryptButton;
    private View encryptButton;
    private View cancelButton;

    private String encryptedValue;
    private Goldfinger goldfinger;
    private OnTextChangedListener onTextChangedListener = new OnTextChangedListener() {
        @Override
        void onTextChanged(String text) {
            encryptButton.setEnabled(!text.isEmpty() && goldfinger.hasEnrolledFingerprint());
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
        goldfinger = new Goldfinger.Builder(this).setLogEnabled(BuildConfig.DEBUG).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (goldfinger.hasFingerprintHardware()
            && goldfinger.hasEnrolledFingerprint()) {
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
        goldfinger.authenticate(new Goldfinger.Callback() {
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

    private void decryptEncryptedValue() {
        cancelButton.setEnabled(true);
        goldfinger.decrypt(KEY_NAME, encryptedValue, new Goldfinger.Callback() {
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
        goldfinger.encrypt(KEY_NAME, secretInputView.getText().toString(), new Goldfinger.Callback() {
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButton.setEnabled(false);
                goldfinger.cancel();
            }
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
