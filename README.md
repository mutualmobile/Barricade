# Barricade

[![Build Status](https://travis-ci.org/mutualmobile/Barricade.svg)](https://travis-ci.org/mutualmobile/Barricade)
[![Version](https://api.bintray.com/packages/mutualmobile/Android/barricade/images/download.svg)](https://bintray.com/mutualmobile/Android/barricade)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.mutualmobile/barricade/badge.svg)](http://www.javadoc.io/doc/com.mutualmobile/barricade)

Barricade is a library that allows you to get responses to api requests locally by running a local server. Barricade will intercept your api calls using a 
OkHttp Network Interceptor and can provide a local response from a local Json.<br /> 
It also supports multiple responses per request, unlike other local server implementations and presents the user with an UI for modifying which response to return for a request. 
at runtime<br/><br/>
**Barricade works only with Retrofit-OkHttp**

## When to use 
* During **development** barricade is useful for easily exercising all edge cases and responses of a feature without needing to adjust the responses from the server.
* For **Unit and Integration Tests**, Barricade allows you to easily toggle through each predefined response for a request to cover every possible edge cases.


## Installing Barricade

Include the following dependencies in your build.gradle:

```
dependencies {
    compile 'com.mutualmobile:barricade:0.1.0'
    apt 'com.mutualmobile:barricade-compiler:0.1.0'
}
```

## How to Use

1. Initialize Barricade using `Barricade.Builder` in your `Application` class in `#onCreate()`

```
@Override
public void onCreate() {
    super.onCreate();
    new Barricade.Builder(this, new BarricadeConfig()).enableShakeToStart(this).install();
    //....
}
```
2. Add your json response files to `assets/barricade`. You might consider subdirectories for each request and put json for each type of response (success, invalid, error etc).

3. In your Retrofit API interface, for the required methods, add annotation `@Barricade` which mentions which endpoint to intercept
 and the possible responses using `@Response` annotation.<br/>
 The `@Response` annotation takes the json file name of the json to refer for the response<br/>
 Example - 
```
  @GET("/users/{user}/repos") 
  @Barricade(endpoint = "repos", responses = {
        @Response(fileName = "get_repos_success", isDefault = true),
        @Response(fileName = "get_repos_invalid", isDefault = false)
    })
    Call<List<Repo>> getUserRepos(@Path("user") String user);
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
the list of responses. This list is populated from the json files in Assets folder



