# In My Fridge

This is the final project for Android Developer Nanodegree program.

## Features

With the app, you can:
* Search for food recipes using ingredients you select
* Search recipes based on calories, intolerances, excluded ingredients
* Show ingredients, steps and nutrition facts
* Search recipes from home screen widget
* Share recipes to your friends

## How to Work with the Source

This app uses [The Spoonacular](https://rapidapi.com/spoonacular/api/Recipe%20-%20Food%20-%20Nutrition) Food API to search for recipes.
You must provide your own API key in order to build the app. When you get it, just paste it at the API_KEY variable in :
    ```
    gradle.properties
    ```
Also, to build and release the app you should provide KEY_ALIAS, KEY_PASSWORD, STORE_FILE, STORE_PASSWORD in :
    ```
    gradle.properties
    ```

## Screens

![screen](../master/screens/screen1.png)

![screen](../master/screens/screen2.png)

![screen](../master/screens/screen3.png)

![screen](../master/screens/screen4.png)

![screen](../master/screens/screen5.png)

![screen](../master/screens/screen6.png)

![screen](../master/screens/screen7.png)

## Libraries

* [Glide](https://bumptech.github.io/glide/)
* [Retrofit](https://github.com/square/retrofit)
* [Butter Knife](http://jakewharton.github.io/butterknife/)
* [Firebase](https://firebase.google.com/)
* [Cloud Storage](https://firebase.google.com/products/storage/)
* [Realtime Database](https://firebase.google.com/products/realtime-database/)

