# Barricade

[![Build Status](https://travis-ci.org/mutualmobile/Barricade.svg)](https://travis-ci.org/mutualmobile/Barricade)
[![Version](https://api.bintray.com/packages/mutualmobile/Android/barricade/images/download.svg)](https://bintray.com/mutualmobile/Android/barricade)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.mutualmobile/barricade/badge.svg)](http://www.javadoc.io/doc/com.mutualmobile/barricade)

Barricade is a library for Android apps that allows you to get responses to API requests locally by running a local server. Barricade will intercept your API calls using an OkHttp Network Interceptor and can provide a local response from a local file.


It also supports multiple responses per request, unlike other local server implementations and presents the user with an UI for modifying which response to return for a request.
at runtime.


**Barricade is currently in beta and works only with Retrofit-OkHttp at the moment.**


## When to use

* During **development**, Barricade is useful for easily exercising all edge cases and responses of a feature without waiting for those server devs to finish implementing the APIs

* For **integration and system tests**, Barricade allows you to easily toggle through each predefined response for a request to cover every possible edge cases

* To build a **demo mode** so that users can explore the app with dummy data


## Adding Barricade to your project

Include the following dependencies in your app's build.gradle :

```
dependencies {
    compile 'com.mutualmobile:barricade:0.1.0'
    apt 'com.mutualmobile:barricade-compiler:0.1.0'
}
```

## How to use

1. Install Barricade in your `Application` class' `#onCreate()`

  ```
  @Override
  public void onCreate() {
      super.onCreate();
      new Barricade.Builder(this, new BarricadeConfig()).enableShakeToStart(this).install();
      ...
  }
  ```

2. Add your API response files to `assets/barricade/` for each type of response (success, invalid, error etc). You should consider creating subdirectories for each endpoint and putting the responses in them to organise them properly.

3. In your Retrofit API interface, for the required methods, add annotation `@Barricade` which mentions which endpoint to intercept and the possible responses using `@Response` annotation.

  Example -
  ```
  @GET("/users/{user}/repos")
  @Barricade(endpoint = "repos", responses = {
          @Response(fileName = "get_repos_success", isDefault = true),
          @Response(fileName = "get_repos_invalid", isDefault = false)
  })
  ...
  ```
4. Add `BarricadeInterceptor` to your `OkHttpClient`

  ```
  OkHttpClient okHttpClient =
        new OkHttpClient.Builder().addInterceptor(new BarricadeInterceptor())
            ...
            .build();
  ```

#### Enable/ Disable Barricade
Barricade can be enabled or disabled at runtime.
* To enable - `Barricade.getInstance().enable()`
* To disable - `Barricade.getInstance().disable();`

#### Change Settings
Barricade allows you to change the response time and the required response for a request at runtime.
* Open Barricade settings by shaking the device
* Change the response time in milliseconds by clicking on the timer on top right
* To change the required response for a request, click on the request from list and then select the response you want from
the list of responses. This list is populated from the response files in assets folder

You can also change the above settings programmatically which can be helpful for testing - 
```
Barricade.getInstance().enable().setDelay(100).withResponse(BarricadeConfig.Endpoints.REPOS, BarricadeConfig.Responses.Repos.GetReposSuccess);; 
```
* `withResponse()` changes the response of the endpoint passed in the first parameter. 


**Note:** Using the above technique will also save the settings and apply to other responses as well, not just the
next response, until changed.