package co.infinum.goldfinger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.biometric.BiometricManager;
import androidx.fragment.app.FragmentActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ValidateUtilsTest {

    private static final String TITLE = "Title";
    private static final String SUBTITLE = "Subtitle";
    private static final String DESCRIPTION = "Description";
    private static final String NEGATIVE_BUTTON_TEXT = "Cancel";
    private static final String KEY = "Key";
    private static final String VALUE = "Value";

    @Mock
    FragmentActivity activity;

    @Test
    public void auth_invalid_emptyTitle() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .description(DESCRIPTION)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .subtitle(SUBTITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .confirmationRequired(true)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).size());
    }

    @Test
    public void auth_invalid_negativeButtonSet() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .description(DESCRIPTION)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .subtitle(SUBTITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .confirmationRequired(true)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).size());
    }

    @Test
    public void auth_invalid_negativeButtonRequired() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .description(DESCRIPTION)
            .title(TITLE)
            .subtitle(SUBTITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .confirmationRequired(true)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).size());
    }

    @Test
    public void auth_invalid_negativeTextRequired() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).size());
    }

    @Test
    public void auth_valid() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .confirmationRequired(true)
            .build();
        assertTrue(ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).isEmpty());
    }

    @Test
    public void auth_valid_missingOptionalParams() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .build();
        assertTrue(ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).isEmpty());
    }

    @Test
    public void auth_valid_negativeTextIgnoredIfDeviceCredentialsTrue() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();
        assertTrue(ValidateUtils.validatePromptParams(Mode.AUTHENTICATION, params).isEmpty());
    }

    @Test
    public void decrypt_invalid_emptyKey() {
        assertEquals(1, ValidateUtils.validateCipherParams(Mode.DECRYPTION, "", VALUE).size());
    }

    @Test
    public void decrypt_invalid_emptyValue() {
        assertEquals(1, ValidateUtils.validateCipherParams(Mode.DECRYPTION, KEY, "").size());
    }

    @Test
    public void decrypt_invalid_withDeviceCredentialsTrue() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.DECRYPTION, params).size());
    }

    @Test
    public void decrypt_valid() {
        assertTrue(ValidateUtils.validateCipherParams(Mode.DECRYPTION, KEY, VALUE).isEmpty());
    }

    @Test
    public void encrypt_invalid_emptyKey() {
        assertEquals(1, ValidateUtils.validateCipherParams(Mode.ENCRYPTION, "", VALUE).size());
    }

    @Test
    public void encrypt_invalid_emptyValue() {
        assertEquals(1, ValidateUtils.validateCipherParams(Mode.ENCRYPTION, KEY, "").size());
    }

    @Test
    public void encrypt_invalid_withDeviceCredentialsTrue() {
        Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
            .title(TITLE)
            .allowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();
        assertEquals(1, ValidateUtils.validatePromptParams(Mode.ENCRYPTION, params).size());
    }

    @Test
    public void encrypt_valid() {
        assertTrue(ValidateUtils.validateCipherParams(Mode.ENCRYPTION, KEY, VALUE).isEmpty());
    }
}