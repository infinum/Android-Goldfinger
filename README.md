# Goldfinger [![JCenter](https://api.bintray.com/packages/infinum/android/goldfinger/images/download.svg)](https://bintray.com/infinum/android/goldfinger/_latestVersion) [![CircleCI Status](https://circleci.com/gh/infinum/Android-Goldfinger/tree/master.svg?style=shield&circle-token=141a7164e06f4e97602260e076110778f16a8d02)](https://bintray.com/infinum/android/goldfinger/_latestVersion)

<img src='./logo.svg' width='264'/>

## Quick guide

#### Add dependency

```gradle
implementation 'co.infinum:goldfinger:1.1.1'
```

#### Initialize

```java
Goldfinger.Builder(context).build()
```

#### Check prerequisites

```java
if (goldfinger.hasEnrolledFingerprint()) {
  /* Authenticate */
}
```

#### Authenticate

```java
goldfinger.authenticate(new Goldfinger.Callback() {

  @Override
  public void onSuccess(String value) {
    /* Authenticated */
  }

  @Override
  public void onError(Error error) {
    /* Error, can be either critical or non-critical */
  }
});
```

You can see all Goldfinger methods [here](./core/src/main/java/co/infinum/goldfinger/Goldfinger.java).

## Rx module

Goldfinger has separate Rx module in case you want to use reactive approach.

#### Add dependencies

```gradle
implementation 'co.infinum:goldfinger:1.1.1'
implementation 'co.infinum:goldfinger-rx:1.1.1'
```

#### Initialize

```java
RxGoldfinger.Builder(context).build()
```

#### Authenticate

```java
goldfinger.authenticate().subscribe(new Observer<GoldfingerEvent>() {

  ...

  @Override
  public void onNext(GoldfingerEvent event) {
    if (event instanceof GoldfingerEvent.OnSuccess) {
      /* Authenticated */
    } else if (event instanceof GoldfingerEvent.OnError) {
      /* Error, can be either critical or non-critical */
    }
  }
});
```

You can see all RxGoldfinger methods [here](./rx/src/main/java/co/infinum/goldfinger/rx/RxGoldfinger.java).

## Fingerprint authentication flow

To use the Android Fingerprint API you must:

- Create a new or load an existing `SecretKey`
- Create a `Cipher` with a created or loaded `SecretKey`
- Create a `CryptoObject` with a created `Cipher`
- Start Fingerprint authentication with a created `CryptoObject`
- Handle possible exceptions at every step due to complexity of the Android Fingerprint API

The `CryptoObject` is **locked** when created and it is **unlocked** when the user successfully authenticates. Once it is unlocked, you can use it to cipher data.

Fingerprint authentication is used to either:

1) Authenticate the user, e.g. for payment
2) Perform encryption or decryption operations over user’s case-sensitive information, e.g. passwords

Goldfinger wraps everything mentioned and provides an intuitive and easy-to-use interface.

## Configuration

If you don’t like Default implementations, you can easily modify them using `Goldfinger.Builder` object.

```java
Goldfinger.Builder(context)
  .setLogEnabled(true)
  .setCryptoCreator(cryptoCreator)
  .setCrypto(crypto)
  .build()
```

#### Logging

Logging is **off** by default. You can enable it by calling `Goldfinger.Builder(context).setLogEnabled(true)`.

#### `CryptoFactory`

Creating a `CryptoObject` is a complicated process that has multiple steps. `CryptoFactory` allows you to modify `CryptoObject` creation and adjust it to your needs.

```java
new CryptoFactory() {

    @Nullable
    @Override
    public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(String keyName) {}

    @Nullable
    @Override
    public FingerprintManagerCompat.CryptoObject createEncryptionCryptoObject(String keyName) {}

    @Nullable
    @Override
    public FingerprintManagerCompat.CryptoObject createDecryptionCryptoObject(String keyName) {}
};
```

All methods should return a `CryptoObject` instance or a `null` value if an error happens during object creation.

You can find the default implementation [here](./core/src/main/java/co/infinum/goldfinger/CryptoFactory.java).

#### Crypto

Goldfinger automatically handles encryption and decryption operations via a `Crypto` implementation which you can implement yourself in case you want a custom cipher.

```java
new Crypto() {

    @Nullable
    @Override
    public String encrypt(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {}

    @Nullable
    @Override
    public String decrypt(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {}
}
```

`Crypto` methods receive an unlocked `CryptoObject` that ciphers data and a `String` value as data you should cipher. The return value should be ciphered data or `null` if an error happens.

The default `Crypto` implementation can be found [here](./core/src/main/java/co/infinum/goldfinger/Crypto.java).

## Known issues

- Android Oreo doesn't throw `KeyPermanentlyInvalidatedException` - [Link](https://issuetracker.google.com/issues/65578763)

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).

## License

```
Copyright 2018 Infinum

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

Maintained and sponsored by [Infinum](http://www.infinum.co).

<a href='https://infinum.co'>
  <img src='https://infinum.co/infinum.png' href='https://infinum.co' width='264'>
</a>
