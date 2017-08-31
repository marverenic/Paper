# Paper

Paper is an RSS reader for Android written in Kotlin. Paper is currently in the early stages of development, so basic functionality is still being implemented. 

## Compiling Paper
Paper doesn't have a stable release yet, so you'll have to compile it from source to try it. This will require you to specify your own Feedly API key. To do this, create a new file in `$PAPER_ROOT_DIR/feedly.properties` with the following entries:
```
feedlyDevUserId = "YOUR-USER-ID"
feedlyDevOauthToken = "YOUR-OAUTH-TOKEN"
feedlyDevApiKey = "YOUR-OPTIONAL-API-KEY"
feedlyApiKey = "YOUR-OPTIONAL-API-KEY"
```
You can acquire your user ID and a developer OAuth token by following the instructions at [Feedly's API documentation](https://developer.feedly.com/v3/developer/). The last two fields are optional. If you don't want to specify values for them, then set them to the empty string (e.g. `feedlyApiKey = ""`).

## License
Paper is licensed under an Apache 2.0 license.