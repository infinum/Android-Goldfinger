package co.infinum.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import co.infinum.goldfinger.rx.GoldfingerEvent;
import co.infinum.goldfinger.rx.RxGoldfinger;
import io.reactivex.observers.DisposableObserver;

public class RxExampleActivity extends AppCompatActivity {

    private static final String KEY_NAME = "Example";

    private View authenticateButton;
    private View decryptButton;
    private View encryptButton;

    private String encryptedValue;
    private RxGoldfinger goldfinger;
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

        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        authenticateButton = findViewById(R.id.authenticateButton);
        secretInputView = findViewById(R.id.secretInputView);
        statusView = findViewById(R.id.statusView);

        goldfinger = new RxGoldfinger.Builder(this).setLogEnabled(BuildConfig.DEBUG).build();

        secretInputView.addTextChangedListener(onTextChangedListener);
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        authenticateButton.setEnabled(goldfinger.hasEnrolledFingerprint());

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
        goldfinger.authenticate().subscribe(new DisposableObserver<GoldfingerEvent>() {

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(GoldfingerEvent goldfingerEvent) {
                onEvent(goldfingerEvent);
            }
        });
    }

    private void decryptEncryptedValue() {
        goldfinger.decrypt(KEY_NAME, encryptedValue).subscribe(new DisposableObserver<GoldfingerEvent>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(GoldfingerEvent goldfingerEvent) {
                onEvent(goldfingerEvent);
            }
        });
    }

    private void encryptSecretValue() {
        goldfinger.encrypt(KEY_NAME, secretInputView.getText().toString()).subscribe(new DisposableObserver<GoldfingerEvent>() {

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(GoldfingerEvent goldfingerEvent) {
                if (goldfingerEvent instanceof GoldfingerEvent.OnSuccess) {
                    decryptButton.setEnabled(true);
                    encryptedValue = ((GoldfingerEvent.OnSuccess) goldfingerEvent).value();
                }
                onEvent(goldfingerEvent);
            }
        });
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

    private void onErrorResult(GoldfingerEvent.OnError event) {
        onResult("onError", event.error().toString());
        if (event.error().isCritical()) {
            statusView.setTextColor(ContextCompat.getColor(this, R.color.error));
        } else {
            statusView.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private void onEvent(GoldfingerEvent goldfingerEvent) {
        if (goldfingerEvent instanceof GoldfingerEvent.OnSuccess) {
            onSuccessResult((GoldfingerEvent.OnSuccess) goldfingerEvent);
        } else if (goldfingerEvent instanceof GoldfingerEvent.OnError) {
            onErrorResult((GoldfingerEvent.OnError) goldfingerEvent);
        }
    }

    private void onResult(String methodName, String value) {
        statusView.setText(String.format(Locale.US, "%s - [%s]", methodName, value));
    }

    private void onSuccessResult(GoldfingerEvent.OnSuccess event) {
        onResult("onSuccess", event.value());
        statusView.setTextColor(ContextCompat.getColor(this, R.color.ok));
    }

    private void resetStatusText() {
        statusView.setTextColor(ContextCompat.getColor(this, R.color.textRegular));
        statusView.setText(getString(R.string.authenticate_user));
    }
}
