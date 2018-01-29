package co.infinum.example;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class OnTextChangedListener implements TextWatcher {

    abstract void onTextChanged(String text);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        onTextChanged(s.toString());
    }
}
