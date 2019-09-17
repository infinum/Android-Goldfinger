package co.infinum.goldfinger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void auth_invalid_negativeTextRequired() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void auth_valid() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .build();
        assertTrue(ValidateUtils.validateParams(params).isEmpty());
    }

    @Test
    public void auth_valid_missingOptionalParams() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .build();
        assertTrue(ValidateUtils.validateParams(params).isEmpty());
    }

    @Test
    public void auth_valid_negativeTextIgnoredIfDeviceCredentialsTrue() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .encrypt(KEY, VALUE)
            .build();
        assertTrue(ValidateUtils.validateParams(params).isEmpty());
    }

    @Test
    public void decrypt_invalid_emptyKey() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .decrypt("", VALUE)
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void decrypt_invalid_emptyValue() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .decrypt(KEY, "")
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void decrypt_valid() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .decrypt(KEY, VALUE)
            .build();
        assertTrue(ValidateUtils.validateParams(params).isEmpty());
    }

    @Test
    public void encrypt_invalid_emptyKey() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .encrypt("", VALUE)
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void encrypt_invalid_emptyValue() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .encrypt(KEY, "")
            .build();
        assertEquals(1, ValidateUtils.validateParams(params).size());
    }

    @Test
    public void encrypt_valid() {
        Goldfinger.Params params = new Goldfinger.Params.Builder(activity)
            .title(TITLE)
            .negativeButtonText(NEGATIVE_BUTTON_TEXT)
            .description(DESCRIPTION)
            .subtitle(SUBTITLE)
            .deviceCredentialsAllowed(true)
            .confirmationRequired(true)
            .encrypt(KEY, VALUE)
            .build();
        assertTrue(ValidateUtils.validateParams(params).isEmpty());
    }
}