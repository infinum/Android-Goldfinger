package co.infinum.goldfinger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

class ValidateUtils {

    private ValidateUtils() {
    }

    /**
     * Return list of errors. If no errors detected, list will be empty.
     */
    @NonNull
    static List<String> validateParams(Goldfinger.PromptParams params) {
        List<String> errors = new ArrayList<>();

        if (!(params.dialogOwner() instanceof Fragment) && !(params.dialogOwner() instanceof FragmentActivity)) {
            errors.add("DialogOwner must be of instance Fragment or FragmentActivity");
        }

        if (StringUtils.isBlankOrNull(params.title())) {
            errors.add("Title is required!");
        }

        if (params.mode() != Mode.AUTHENTICATION) {
            if (StringUtils.isBlankOrNull(params.key())) {
                errors.add("Key is required if encryption or decryption is used!");
            }
            if (StringUtils.isBlankOrNull(params.value())) {
                errors.add("Value is required if encryption or decryption is used!");
            }
        }

        if (!params.deviceCredentialsAllowed() && StringUtils.isBlankOrNull(params.negativeButtonText())) {
            errors.add("NegativeButtonText is required!");
        }

        return errors;
    }
}
