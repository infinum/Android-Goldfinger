# Goldfinger [![JCenter](https://api.bintray.com/packages/infinum/android/goldfinger/images/download.svg)](https://bintray.com/infinum/android/goldfinger/_latestVersion) [![Build Status](https://app.bitrise.io/app/bc0cdf2da387a5c3/status.svg?token=eHOSr1ZB1HzNnKZfxYjxbA&branch=master)](https://bintray.com/infinum/android/goldfinger/_latestVersion)

<img src='./logo.svg' width='264'/>

## Important

This version is compatible with `androidx.biometric`. If you do not want to use `androidx.biometric`, feel free to use [older version of Goldfinger](https://github.com/infinum/Android-Goldfinger/tree/v1.2.1).

This version **requires** you to use Java 8.

```gradle
android {
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}
``` 

## Quick guide

#### Add dependency

```gradle
implementation 'co.infinum:goldfinger:2.0.0-RC1'
```

#### Initialize

```java
Goldfinger.Builder(context).build()
```

#### Check prerequisites

```java
if (goldfinger.canAuthenticate()) {
  /* Authenticate */
}
```

#### Build params

```java
Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(activity)
  .title("Title")
  .negativeButtonText("Cancel")
  .description("Description")
  .subtitle("Subtitle")
  .build();
```

#### Authenticate

```java
goldfinger.authenticate(params, new Goldfinger.Callback() {
    @Override
    public void onError(@NonNull Exception e) {
        /* Critical error happened */
    }

    @Override
    public void onResult(@NonNull Goldfinger.Result result) {
        /* Result received */
    }
});
```

#### Goldfinger.PromptParams

PromptParams are directly linked to [BiometricPrompt.PromptInfo](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder.html) so be sure to read which parameters are required.

You can see all Goldfinger methods [here](./core/src/main/java/co/infinum/goldfinger/Goldfinger.java).

## Rx module

Goldfinger has separate Rx module in case you want to use reactive approach.

#### Add dependencies

```gradle
implementation 'co.infinum:goldfinger:2.0.0-RC1'
implementation 'co.infinum:goldfinger-rx:2.0.0-RC1'
```

#### Initialize

```java
RxGoldfinger.Builder(context).build()
```

#### Authenticate

```java
goldfinger.authenticate(params).subscribe(new DisposableObserver<Goldfinger.Result>() {

  @Override
  public void onComplete() {
    /* Fingerprint authentication is finished */
  }

  @Override
  public void onError(Throwable e) {
    /* Critical error happened */
  }

  @Override
  public void onNext(Goldfinger.Result result) {
    /* Result received */
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
- Create `BiometricPrompt.PromptInfo` object
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
  .logEnabled(true)
  .cryptoObjectFactory(factory)
  .cryptographyHandler(cryptographyHandler)
  .build()
```

#### Logging

Logging is **off** by default. You can enable it by calling `Goldfinger.Builder(context).logEnabled(true)`.

#### `CryptoObjectFactory`

Creating a `CryptoObject` is a complicated process that has multiple steps. `CryptoObjectFactory` allows you to modify `CryptoObject` creation and adjust it to your needs.

```java
new CryptoObjectFactory() {
  @Nullable
  @Override
  public BiometricPrompt.CryptoObject createEncryptionCryptoObject(@NonNull String keyName) {}

  @Nullable
  @Override
  public BiometricPrompt.CryptoObject createDecryptionCryptoObject(@NonNull String keyName) {}
};
```

All methods should return a `CryptoObject` instance or a `null` value if an error happens during object creation.

You can find the default implementation [here](./core/src/main/java/co/infinum/goldfinger/CryptoObjectFactory.java).

#### CryptographyHandler

Goldfinger automatically handles encryption and decryption operations via a `CryptographyHandler` implementation which you can implement yourself in case you want a custom cipher.

```java
new CryptographyHandler() {
  @Nullable
  @Override
  public String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {}

  @Nullable
  @Override
  public String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {}
}
```

`CryptographyHandler` methods receive an unlocked `CryptoObject` and a `String` value. The return value should be ciphered `value` or `null` if an error happens.

The default `CryptographyHandler` implementation can be found [here](./core/src/main/java/co/infinum/goldfinger/CryptographyHandler.java).

## Known issues

- Android Oreo does not throw `KeyPermanentlyInvalidatedException` - [Link](https://issuetracker.google.com/issues/65578763)

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
