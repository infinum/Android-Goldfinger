package co.infinum.goldfinger;

import java.util.List;

/**
 * Thrown if provided parameters are invalid.
 */
class InvalidParametersException extends Exception {

    InvalidParametersException(List<String> errors) {
        super(StringUtils.join(errors));
    }
}
