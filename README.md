# Goldfinger [ ![Download](https://api.bintray.com/packages/infinum/android/goldfinger/images/download.svg) ](https://bintray.com/infinum/android/goldfinger/_latestVersion)

Android library to simplify Fingerprint authentication integration.

## Usage
#### Download
```gradle
implementation 'co.infinum:goldfinger:0.3.3'
```

#### Initialization
```java
Goldfinger goldfinger = Goldfinger.create(Context)
```

#### Use it
```java
if (goldfinger.hasEnrolledFingerprint) {
  goldfinger.authenticate(new Goldfinger.Callback() {
    @Override
    public void onSuccess(String value) {
      //User successfully authenticated.
    }

    @Override
    public void onWarning(Warning warning) {
      //Authentication failed but user can Retry.
    }

    @Override
    public void onError(Error error) {
      //Critical error, authentication canceled.
    }
  });
}
```


## Options

In case you don't want default Fingerprint authentication implementation, you can pass your own implementation via Builder pattern initialization.

Default implementations will be used if custom implementation is not provided.

```java
new Goldfinger.Builder(Context)
  .logger(Logger)
  .cryptoCreator(CryptoCreator)
  .crypto(Crypto)
  .build();
```

#### Logger

Custom logging implementation. Default implementation is empty.

```java
new Logger() {

  @Override
  public void log(Throwable t) {
    //Log throwable
  }

  @Override
  public void log(String message) {
    //Log message
  }
}
```

#### CryptoCreator

Customize CryptoObject creation in case you need different transformation or custom initialization vector (IV) handling.

```java
new CryptoCreator() {

  @Override
  @Nullable
  public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(keyName: String) {
  }

  @Override
  @Nullable
  public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(keyName: String) {
  }

  @Override
  @Nullable
  public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(keyName: String) {
  }
}
```

#### Crypto

Customize encryption/decryption process after user successfully authenticates.

```java
new Crypto() {

  @Override
  @Nullable
  public String encrypt(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {
    //Encrypt value
  }
  @Override
  @Nullable
  public String decrypt(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {
    //Decrypt value
  }
}
```

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).

## Credits

Maintained and sponsored by
[Infinum](http://www.infinum.co).

<img src="https://infinum.co/infinum.png" width="264">