## Running the tests

### Prerequisites 
1. [Java 12](https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html)
1. [Gradle](https://gradle.org/)

### Running the tests
1. Clone the repo `git clone https://github.com/PaithanQ/apitest.git`
1. Browse to the folder `cd apitest`
1. Run `gradle test`
1. To run the tests second and consecutive times without changes in the code `gradle test --rerun
-tasks`
1. For a more descriptive output `gradle -i test` 

## Considerations
* As they are black box tests and I don't have control over the db:
    1. I did not cover cases like return no users listed, as running the tests twice will
     make them fail.
    1. I make some extra calls during the tests to ensure they are in the right state before
     running.
* No consideration over locales or encoding (special characters...) 
* Contrary to the specs the send message endpoint does not need basic auth, so I didn't add it.
* As they were not in the specs, I only covered few negative test cases.
* Same for the forum themes, I only covered one of the valid ones.
* For the sake of the exercise I tried to use the less libraries and more basic ones. Using for
 instance [rest-assured](http://rest-assured.io/) and junit would have made it easier to implement. 
* As I only used asserts from junit (hamcrest), I did not assert json objects, but that the
 json strings contained some substring, that's a bad practice that could be fixed by using any
  other dependency for assertion or rest-assured.
