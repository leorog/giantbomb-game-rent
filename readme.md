# Result

- backend code at api/
 - GET /search?q=giantbombquery
 - POST /rent 
   - contract `{:game-ids [ids]}`
- client code at client/

## Running

At the root dir
```sh
make run
```

This will package client code into api/resources/ and package the backend as a jar that runs jetty serving the client code and the two endpoints 

The default config should bind to 8080, the client application can be found at http://localhost:8080/index.html

# Gravie Software Engineer Challenge

## Instructions
After completing the challenge below, please send us an email with the location of your repository. If your repository is private, be sure to add us as collaborators so we can view your code.

### Time Box
3-4 Hours

## Synopsis

For this challenge you will implement the Giant Bomb API to create an application that will allow a user to search games and "rent" them. The application should consist of at least two unique pages (`search` and `checkout`). Your view should display the game thumbnail and title, and the rest is up to you. You can use any language and or framework you'd like.

![Giant Bomb](https://upload.wikimedia.org/wikipedia/en/4/4b/Giant_Bomb_logo.png)

You can get started by signing up for an API key [here](https://www.giantbomb.com/api/).

### Resources

You can find the quickstart guide [here](https://www.giantbomb.com/forums/api-developers-3017/quick-start-guide-to-using-the-api-1427959/).

You can find a full list of API features [here](https://www.giantbomb.com/api/documentation).

### Questions

Don't hesitate to reach out with any questions. Remember we are more focused on seeing your development process than checking off a list of requirements, so be sure you are able to speak to your code and your thoughts behind it.
