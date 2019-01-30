package co.infinum.goldfinger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

/**
 * Data wrapper with Builder pattern for easier library usage.
 *
 * @see Builder
 */
@SuppressWarnings("WeakerAccess")
public class GoldfingerParams {

    private final FragmentActivity activity;
    private final CryptographyData cryptographyData;
    private final BiometricPrompt.PromptInfo promptInfo;

    private GoldfingerParams(
        FragmentActivity activity,
        BiometricPrompt.PromptInfo promptInfo,
        CryptographyData cryptographyData
    ) {
        this.activity = activity;
        this.promptInfo = promptInfo;
        this.cryptographyData = cryptographyData;
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public CryptographyData getCryptographyData() {
        return cryptographyData;
    }

    public BiometricPrompt.PromptInfo getPromptInfo() {
        return promptInfo;
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private FragmentActivity activity;
        private CryptographyData cryptographyData;
        private String description;
        private String negativeButtonText;
        private BiometricPrompt.PromptInfo promptInfo;
        private String subtitle;
        private String title;

        public Builder(@NonNull FragmentActivity activity) {
            this.activity = activity;
        }

        @NonNull
        public GoldfingerParams build() {
            return new GoldfingerParams(
                activity,
                promptInfo != null ? promptInfo : buildBiometricPromptInfo(),
                cryptographyData
            );
        }

        /**
         * Required: For {@link Goldfinger#encrypt(GoldfingerParams, GoldfingerCallback)}
         * and {@link Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)}
         * <p>
         * Ignored: For {@link Goldfinger#authenticate(GoldfingerParams, GoldfingerCallback)}
         *
         * @see CryptographyData
         */
        @NonNull
        public Builder cryptographyData(@NonNull CryptographyData cryptographyData) {
            this.cryptographyData = cryptographyData;
            return this;
        }

        /**
         * @see #cryptographyData(CryptographyData)
         */
        @NonNull
        public Builder cryptographyData(@NonNull String keyName, @NonNull String value) {
            return cryptographyData(new CryptographyData(keyName, value));
        }

        /**
         * Optional.
         *
         * @see BiometricPrompt.PromptInfo.Builder#setDescription(CharSequence)
         */
        @NonNull
        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        /**
         * Required, if no value is given, uses default value.
         *
         * @see BiometricPrompt.PromptInfo.Builder#setNegativeButtonText(CharSequence)
         */
        @NonNull
        public Builder negativeButtonText(@NonNull String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * Instead of calling single methods on {@link GoldfingerParams.Builder} you can
         * immediately pass already built {@link BiometricPrompt.PromptInfo}. In this case,
         * it will override {@link GoldfingerParams.Builder#title}, {@link GoldfingerParams.Builder#subtitle},
         * {@link GoldfingerParams.Builder#description} and {@link GoldfingerParams.Builder#negativeButtonText}.
         */
        @NonNull
        public Builder promptInfo(@Nullable BiometricPrompt.PromptInfo promptInfo) {
            this.promptInfo = promptInfo;
            return this;
        }

        /**
         * Optional.
         *
         * @see BiometricPrompt.PromptInfo.Builder#setSubtitle(CharSequence)
         */
        @NonNull
        public Builder subtitle(@Nullable String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * Required, if no value is given, uses default value.
         *
         * @see BiometricPrompt.PromptInfo.Builder#setTitle(CharSequence)
         */
        @NonNull
        public Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }

        private BiometricPrompt.PromptInfo buildBiometricPromptInfo() {
            BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title != null ? title : "<TITLE>")
                .setNegativeButtonText(negativeButtonText != null ? negativeButtonText : "<NEGATIVEBUTTONTEXT>");
            if (subtitle != null) {
                builder.setSubtitle(subtitle);
            }
            if (description != null) {
                builder.setDescription(description);
            }
            return builder.build();
        }
    }
}
