# Gift & Go Assessment

This is the Gift & Go assessment submission.

The acceptance criteria for the mandatory and additional tasks have been implemented. 

The project uses Spring Boot 3 with Kotlin.


## Running

### Running with Docker Compose

The app can be spun up by running docker compose first (this sets up MySQL): 
```
docker compose up
```  

Next the build can be executed with the following:

```
./gradlew build
```

And then running the app:
```
./gradlew bootRun
```

### Running the tests

The unit tests can be run using the following:
```
./gradlew test
```

The integration tests can be run using the following:
```
./gradlew integrationTest
```

## Potential Improvements
Due to time constraint some corners were cut.
* Errors returned by result objects are plain strings, they could be properly typed as enums.
* The integration test could be split out into happy and sad path classes. Additionally, the http client has no individual test. 
* Some logging could be introduced to trace requests in the application.
* Proper separation of config could be introduced. Right now all the config is under `application`.
* Unit tests do not cover verify all possible side effects.
