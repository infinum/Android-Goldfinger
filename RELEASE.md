## Release guide

### Define environment variables

You must define **BINTRAY_USER** and **BINTRAY_API_KEY** as environment variable.

Bintray API key can be found under Edit Profile -> API Key.

### Check README.md and build.gradle

Be sure that [build.gradle](https://github.com/infinum/Android-Goldfinger/blob/master/build.gradle) and [Readme](https://github.com/infinum/Android-Goldfinger/blob/master/README.md) are both updated and contain new, to be released version.

### Run gradle task

`./gradlew clean build javadocs assemble`
`./gradlew bintrayUpload`

### Bintray publish

Manually go to [Bintray page](https://bintray.com/infinum/android), check that all 8 files are there and publish them if everything is OK.
