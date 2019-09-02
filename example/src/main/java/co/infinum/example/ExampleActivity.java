package co.infinum.example;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;

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
    private Goldfinger.Callback callback = new Goldfinger.Callback() {
        @Override
        public void onError(@NonNull Exception e) {
            onGoldfingerError();
        }

        @Override
        public void onResult(@NonNull Goldfinger.Result result) {
            onGoldfingerResult(result);
        }
    };

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

        if (goldfinger.hasFingerprintHardware()) {
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
        GoldfingerParams params = baseParams().build();
        goldfinger.authenticate(params, callback);
        goldfinger.authenticate(params, callback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goldfinger.authenticate(params, callback);
            }
        }, 2000);
    }

    private GoldfingerParams.Builder baseParams() {
        return new GoldfingerParams.Builder(this)
            .description("Description")
            .subtitle("Subtitle")
            .title("Title")
            .negativeButtonText("Cancel");
    }

    private void decryptEncryptedValue() {
        cancelButton.setEnabled(true);
        GoldfingerParams params = baseParams().cryptographyData(KEY_NAME, encryptedValue).build();
        goldfinger.decrypt(params, callback);
    }

    private void encryptSecretValue() {
        cancelButton.setEnabled(true);
        GoldfingerParams params = baseParams().cryptographyData(KEY_NAME, secretInputView.getText().toString()).build();
        goldfinger.encrypt(params, callback);
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
