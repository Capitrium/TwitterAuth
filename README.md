TwitterAuth
=======================

A wrapper for the [Twitter4j 3.0.5](http://twitter4j.org/en/index.html) library.

This is a library project which aims to simplify the login process for Android apps which provide the option to login via Twitter.

--------------------
## Setup Project
1. Download [twitter4j-core-3.0.5.jar](http://twitter4j.org/en/index.html#download) and place it in your project's libs directory.

2. Clone this project and import it into Eclipse as an existing Android project.

3. Update the `TwitterValues` class and change the values for `TWITTER_CONSUMER_KEY` and `TWITTER_CONSUMER_SECRET` to the values listed on the Twitter developer console for your app.

4. Clean and build the `TwitterAuth` project, then copy the `twitterauth.jar` file from the `bin` directory into your project's `libs` directory. You may also copy the `twitterauth-docs` directory and `twitterauth.jar.properties` file from the `TwitterAuth` project into your project's `libs` directory to enable javadoc tooltips in Eclipse.

5. Update the `manifest.xml` file and add the following to the intent-filter for the activity which launches the Twitter login webpage:

	```
	<category android:name="android.intent.category.BROWSABLE" />
	```

6. Also add the following to the intent-filter for the activity that should launch after the user returns from the Twitter login page:
	
	```
	<data android:scheme="oauth" android:host="twitter.login" />
	```

## Example

An example application is included in the `sample-project` directory, which can be imported into Eclipse as an existing Android project; feel free to run this application on your device and look at the source code if you are unclear as to how the library should be used.