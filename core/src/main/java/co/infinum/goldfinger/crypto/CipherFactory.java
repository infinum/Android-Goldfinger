package co.infinum.goldfinger.crypto;

import javax.crypto.Cipher;

/**
 * @see Factory
 * @see co.infinum.goldfinger.crypto.impl.AesCipherFactory
 * @see co.infinum.goldfinger.crypto.impl.UnlockedAesCipherFactory
 */
public interface CipherFactory extends Factory<Cipher> {
}
