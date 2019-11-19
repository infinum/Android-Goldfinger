package co.infinum.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(Build.VERSION_CODES.M)
public class SetPinActivity extends AppCompatActivity {

    private EditText pinInputView;
    private View setPinButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pin = SharedPrefs.getPin();
        if ("".equals(pin)) {
            setContentView(R.layout.activity_set_pin);
            initViews();
            initListeners();
        } else {
            navigateToLogin();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSetPinState();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void initListeners() {
        pinInputView.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                updateSetPinState();
            }
        });

        setPinButton.setOnClickListener(v -> {
            SharedPrefs.setPin(pinInputView.getText().toString());
            navigateToLogin();
        });
    }

    private void initViews() {
        pinInputView = findViewById(R.id.pinInputView);
        setPinButton = findViewById(R.id.setPinButton);
    }

    private void navigateToLogin() {
        Class<?> cls = SharedPrefs.isRxExample() ? RxLoginActivity.class : LoginActivity.class;
        startActivity(new Intent(this, cls));
        finish();
    }

    private void updateSetPinState() {
        setPinButton.setEnabled(pinInputView.getText().toString().length() == 4);
    }
}
