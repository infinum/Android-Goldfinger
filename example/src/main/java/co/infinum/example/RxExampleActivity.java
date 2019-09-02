package co.infinum.example;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;
import co.infinum.goldfinger.rx.RxGoldfinger;
import io.reactivex.observers.DisposableObserver;

public class RxExampleActivity extends AppCompatActivity {

    private static final String KEY_NAME = "Example";

    private View authenticateButton;
    private View decryptButton;
    private View encryptButton;
    private View cancelButton;

    private String encryptedValue;
    private RxGoldfinger goldfinger;
    private OnTextChangedListener onTextChangedListener = new OnTextChangedListener() {
        @Override
        void onTextChanged(String text) {
            encryptButton.setEnabled(!text.isEmpty() && goldfinger.hasFingerprintHardware());
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
        goldfinger = new RxGoldfinger.Builder(this).setLogEnabled(BuildConfig.DEBUG).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        authenticateButton.setEnabled(goldfinger.hasFingerprintHardware());

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
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .description("Description")
            .subtitle("Subtitle")
            .build();
        goldfinger.authenticate(params).subscribe(new DisposableObserver<Goldfinger.Result>() {

            @Override
            public void onComplete() {
                cancelButton.setEnabled(false);
            }

            @Override
            public void onError(Throwable e) {
                onGoldfingerError();
            }

            @Override
            public void onNext(Goldfinger.Result result) {
                onGoldfingerResult(result);
            }
        });
    }

    private void decryptEncryptedValue() {
        cancelButton.setEnabled(true);
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .description("Description")
            .subtitle("Subtitle")
            .cryptographyData(KEY_NAME, encryptedValue)
            .build();
        goldfinger.decrypt(params).subscribe(new DisposableObserver<Goldfinger.Result>() {
            @Override
            public void onComplete() {
                cancelButton.setEnabled(false);
            }

            @Override
            public void onError(Throwable e) {
                onGoldfingerError();
            }

            @Override
            public void onNext(Goldfinger.Result result) {
                onGoldfingerResult(result);
            }
        });
    }

    private void encryptSecretValue() {
        cancelButton.setEnabled(true);
        GoldfingerParams params = new GoldfingerParams.Builder(this)
            .description("Description")
            .subtitle("Subtitle")
            .cryptographyData(KEY_NAME, secretInputView.getText().toString())
            .build();
        goldfinger.encrypt(params).subscribe(new DisposableObserver<Goldfinger.Result>() {

            @Override
            public void onComplete() {
                cancelButton.setEnabled(false);
            }

            @Override
            public void onError(Throwable e) {
                onGoldfingerError();
            }

            @Override
            public void onNext(Goldfinger.Result result) {
                onGoldfingerResult(result);
                if (result.type() == Goldfinger.Type.SUCCESS) {
                    decryptButton.setEnabled(true);
                    encryptedValue = result.value();
                }
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
