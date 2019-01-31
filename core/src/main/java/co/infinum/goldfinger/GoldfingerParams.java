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
@SuppressWarnings({"WeakerAccess", "unused"})
public class GoldfingerParams {

    private final FragmentActivity activity;
    private final CryptographyData cryptographyData;
    private final String description;
    private final String negativeButtonText;
    private final String subtitle;
    private final String title;

    private GoldfingerParams(
        @NonNull FragmentActivity activity,
        @NonNull CryptographyData cryptographyData,
        @NonNull String title,
        @NonNull String description,
        @NonNull String negativeButtonText,
        @NonNull String subtitle
    ) {
        this.activity = activity;
        this.cryptographyData = cryptographyData;
        this.title = title;
        this.description = description;
        this.negativeButtonText = negativeButtonText;
        this.subtitle = subtitle;
    }

    @NonNull
    public FragmentActivity getActivity() {
        return activity;
    }

    @NonNull
    public CryptographyData getCryptographyData() {
        return cryptographyData;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    @NonNull
    public String getSubtitle() {
        return subtitle;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    BiometricPrompt.PromptInfo buildPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .build();
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private FragmentActivity activity;
        private CryptographyData cryptographyData;
        private String description;
        private String negativeButtonText;
        private String subtitle;
        private String title;

        public Builder(@NonNull FragmentActivity activity) {
            this.activity = activity;
        }

        @NonNull
        public GoldfingerParams build() {
            return new GoldfingerParams(
                activity,
                cryptographyData != null ? cryptographyData : new CryptographyData("", ""),
                title != null ? title : "",
                description != null ? description : "",
                negativeButtonText != null ? negativeButtonText : "",
                subtitle != null ? subtitle : ""
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
         * @see BiometricPrompt.PromptInfo.Builder#setDescription(CharSequence)
         */
        @NonNull
        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        /**
         * @see BiometricPrompt.PromptInfo.Builder#setNegativeButtonText(CharSequence)
         */
        @NonNull
        public Builder negativeButtonText(@NonNull String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * @see BiometricPrompt.PromptInfo.Builder#setSubtitle(CharSequence)
         */
        @NonNull
        public Builder subtitle(@Nullable String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * @see BiometricPrompt.PromptInfo.Builder#setTitle(CharSequence)
         */
        @NonNull
        public Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }
    }
}
