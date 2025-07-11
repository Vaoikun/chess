# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Phase 2


[Server Design](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVwwFpHaJpUQH+iMykoKp+h-Dsmnafp8K+s6XYwAgQnihignCQSRJgKS2k6YOVK3vyDJMlORmedpw4OkuwowGKEpujKcplu8SqYCazAAHIQBu7raDAABmvhNl5b6GEmlSifUDTpsE3TZvyebzNBRYlvUACSaBUCaSDrkZDYoAgBiZTKeU+AVoV+Yu4lUEiMA9n2mCFbZgpFfU7XIFo6JNKZ6kYmpOxrLF2ikilMCtStnUwIeHDmEgJoaPNEnwsmyCpo06YAIzVWMOaqHVBZjI10D1MNFHQEgABeKC7HRfWGAAah164XVdN1zdpRWCmF9LnROKDPvE56XteXkYwKEX1I+AYE1ud2Te+emPHC9QueKGSqABKNaVZIHVAZhHlsR0GwZeVH1iRqENtTGElfAT3YTAuH4aMvOJfzZFjBRwvIaLtGNgx9E64x3h+P4XgoOgMRxIkJtmy5vhYKJi3c6WDTSBG-ERu0EbdD0cmqApwzq4h6Dszpkt07C-oB0hwecxNU0OfYtvOUJttuWoHnB2jscujAp1rTjl74-BgdoIdCDMCd8PGpeMCQFH1ODo9JRgOVb0fV9P0NcWAMKtMl4g+DkNmNDMBw6tVd6jXxcZwtu40vu9SXdwx4F5H6AhVpxP3pFGQzBANAUy+VOjXPd5S0t029v29e0w84eljNV+WfTksTf6D-T-dmEy83ct4VmdFNj1p4Q2AQUTrn8NgcUGp+JohgAAcSVBoe2r8nZwPdl7ewSp-ZCynhLayt8vz1FXoULyMcbKdnqMgHICCcyF0osXde3kdzkJpnZXOch85nmIaXcubVK4UUnnXVGM9G7PQqk4d6MAaq5nzJ3JqPdgZQDBhDHqw9R5nQEbXIO19iq+RPv5GAjIwC4zoRrRhm9SZRXFE+Sm8hZTymIcHCxWc7K4w9Do9GY1RyGKZDQtQMCcgYnMV4re9Q4FMngYggSaInEhLPrZMJiCAlYA8VLKyiSczJOjs-B6LCDLLEwTmAsDRxiFJQM1aQBZXrhGCIEEEmx4i6hQG6TkexvjJFAGqFpkFFjfDKalJUvSLgwE6BZLSoEv5Nxwn-UYBTEHFNKUqCpVSal1OWA0pp3TPi9JBB0kAXSiL1TGH0pUAy5hDJGQA3WTZgHMX8BwAA7G4JwKAnAxAjMEOAXEABs8BsaRLmDAIo38Hb3XKq0DoGCsG9yLkhLM-SlRjJDvg9J496FITWAiuY2S76gtYfUQ8HCMRwGxn41OJJkplxgAAWWyL7BwPRxS50MRWNawAP6sMmWI9MTg261VkYWLupY9DrhRISNOqj+rqPXGyjlPkmFsPhhwkx3DKW8OZZo3BwjOxctluIyR0jvoCr+kKwGML4j9xUVDKV-Dq5aJIdqzls9eQjgCsYleOCkLBP0YuSoy4rESjcdleKjiiZxJcWTWx7LUl6JdQYwl6I-GmOLpi05SpvVxt9UKMma4AViDDT6kmEaL6zVSR+HJ9QSVHhQGS-8ZccVfi5mCqRcy5jLPqNU2pMAkUTKlmVX+CsRitvKZUjtqzu36z1kApiRtLC9Qcpsc2SAEhgDnX2CAi6ABSEBxR5piHstUwKm54skk0ZkMkehlOwbC9AWZsAIGAHOqAcAIAOSgKmtt0ge2O0FKi4hqkH1PpfW+j9I6G3AWLQAKx3WgJN-6YD3sfZQYD0BQMVJ4TSul4p7CMr4WPMpRgj7jJEaVb+Lc8J8pkfVQV8iRUwDFe5SVsNK6ypjQq5aSrl5cM9WvNVx08MaLtVq4jOq+1kZehIyjRrqMmvkUDPuSiB5MZHraie9q5XMJkF4t1KqeMl1iYW0JAabGHzsSGvTBnM1FpYVNIN8gNO6K04Wt1SaynocswuEmfrt5MiUSgfefi4ryjc9IDz+4m34rzR6I6fjKxmnlNB8UEN7HGjQBAPUGFENPoc7qn+4iqpSM+vymT-1SwmnizXcMyFrWGFixAbA2jHXyuJkQtcTRAOUAQq5pZ0gANIayvIDNnmjPMmwHnKL2UdQTyy5uezBarMRamu1-rj4IY5bSRWmA27xS1tZvW0hOSItv0vhyl+pGpkDv-pOwBN2GK3KNl4R9S6V2PflIgYMsBgDYHvYQPIBQgXILyU7F2bsPZe2MKdlFm33t4B5HoAw5KUlP1xcWkA3A8DEvRwN3Q+gUCI8YZnWNnmCVY6nEN8Llid570MCaBACV3FNc0y186WOTHk9Pt5+oVP9607RRrW6jPHPM7Rx9vxqgglhY59mmA3Oad9jzQLkTTry130rVjuHuOWZswOyjlB3YTt4Ny9MwdVz9ZAA)

