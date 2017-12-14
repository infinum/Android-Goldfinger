package co.infinum.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.Logger;
import co.infinum.goldfinger.Warning;

public class ExampleActivity extends AppCompatActivity {

    EditText secretInput;
    View encryptButton;
    View decryptButton;
    View authenticateButton;
    TextView encryptedTextView;
    TextView decryptedTextView;
    TextView statusTextView;
    Goldfinger goldfinger;
    String encryptedValue = "";
    String decryptedValue = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        secretInput = findViewById(R.id.secretInput);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        authenticateButton = findViewById(R.id.authenticateButton);
        encryptedTextView = findViewById(R.id.encryptedTextView);
        decryptedTextView = findViewById(R.id.decryptedTextView);
        statusTextView = findViewById(R.id.statusTextView);
        goldfinger = new Goldfinger.Builder(this).logger(new Logger() {
            @Override
            public void log(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void log(String message) {
                Log.e("Goldfinger", message);
            }
        }).build();
        initListeners();
    }

    private void initListeners() {
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTextView.setText("Waiting for finger");
                goldfinger.encrypt("customKey", secretInput.getText().toString(), new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {
                        encryptedValue = value;
                        encryptedTextView.setText("Encrypted value - " + value);
                    }

                    @Override
                    public void onWarning(Warning warning) {
                        statusTextView.setText("Warning - " + warning.name());
                    }

                    @Override
                    public void onError(Error error) {
                        statusTextView.setText("Error - " + error.name());
                    }
                });
            }
        });
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTextView.setText("Waiting for finger");
                goldfinger.decrypt("customKey", encryptedValue, new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {
                        decryptedValue = value;
                        decryptedTextView.setText("Decrypted value - " + value);
                    }

                    @Override
                    public void onWarning(Warning warning) {
                        statusTextView.setText("Warning - " + warning.name());
                    }

                    @Override
                    public void onError(Error error) {
                        statusTextView.setText("Error - " + error.name());
                    }
                });
            }
        });

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTextView.setText("Waiting for finger");
                goldfinger.authenticate(new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {
                        statusTextView.setText("User authenticated");
                    }

                    @Override
                    public void onWarning(Warning warning) {
                        statusTextView.setText("Warning - " + warning.name());
                    }

                    @Override
                    public void onError(Error error) {
                        statusTextView.setText("Error - " + error.name());
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean fingerprintAllowed = true;
        String statusText = "Allowed";
        if (!goldfinger.hasFingerprintHardware()) {
            statusText = "No fingerprint hardware.";
            fingerprintAllowed = false;
        } else if (!goldfinger.hasEnabledLockScreen()) {
            statusText = "Lock screen not enabled.";
            fingerprintAllowed = false;
        } else if (!goldfinger.hasEnrolledFingerprint()) {
            statusText = "No enrolled fingerprints";
            fingerprintAllowed = false;
        }

        if (!fingerprintAllowed) {
            disableView(secretInput);
            disableView(encryptButton);
            disableView(decryptButton);
        }
        statusTextView.setText(statusText);
    }

    public void disableView(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }

    public void enableView(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }
}
