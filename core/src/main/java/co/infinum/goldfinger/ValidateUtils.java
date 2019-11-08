package co.infinum.goldfinger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

class ValidateUtils {

    private ValidateUtils() {
    }

    /**
     * Return list of cipher params errors. If no errors detected, list will be empty.
     */
    @NonNull
    static List<String> validateCipherParams(@NonNull Mode mode, @Nullable String key, @Nullable String value) {
        List<String> errors = new ArrayList<>();

        if (mode != Mode.AUTHENTICATION) {
            if (StringUtils.isBlankOrNull(key)) {
                errors.add("Key is required if encryption or decryption is used!");
            }
            if (StringUtils.isBlankOrNull(value)) {
                errors.add("Value is required if encryption or decryption is used!");
            }
        }

        return errors;
    }

    /**
     * Return list of prompt params errors. If no errors detected, list will be empty.
     */
    @NonNull
    static List<String> validatePromptParams(@NonNull Mode mode, @NonNull Goldfinger.PromptParams params) {
        List<String> errors = new ArrayList<>();

        if (!(params.dialogOwner() instanceof Fragment) && !(params.dialogOwner() instanceof FragmentActivity)) {
            errors.add("DialogOwner must be of instance Fragment or FragmentActivity");
        }

        if (StringUtils.isBlankOrNull(params.title())) {
            errors.add("Title is required!");
        }

        if (!params.deviceCredentialsAllowed() && StringUtils.isBlankOrNull(params.negativeButtonText())) {
            errors.add("NegativeButtonText is required!");
        }

        if (params.deviceCredentialsAllowed() && mode != Mode.AUTHENTICATION) {
            errors.add("DeviceCredentials are allowed only for Goldfinger#authenticate method.");
        }

        return errors;
    }
}
