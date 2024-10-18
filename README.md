# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Sequence Diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpgwR0XUlDAFrOxclOX9g1AjYxNTs33zqotQy89rfRtbOx+B2Ol043FgF2EVFE5SgURimSgAApItFYpRIgBHHxqMAASkwwAQzAAsjFVEgHC0AJJoKhEpAcIzaQ7HKGic4XbLmcoVAAsTiczX6o3UwHsTymAFEoN4yjA9Ey4djcX0DigEAYYAA1BlMrTyVlHdkqc6FWTyJQqdTlCVgACqHSR90eBJBFsUyjUqjNRh05QAYpSmY7KB6FWEXcBRoTiTBQw9o4YiXDgBwwvIANboI0naGm3Jc5A8yoCgDMwr6otU4slfRlcvKKZQaYzwGzaDVZk1hgTjxgaAgzCzOZBJsMFw9Vu95RAqcyCedHQ9buOU696l9xnKCg4IeXLLHxQ5k+005tMDnLcyCh8YFSSOAd9SK9z6+tPou25gu6Zt-vHq5uOnKFGwYI3BE8LolAkSqD4xK5mB1wQpCxQQXcHSPJM5SrK8T73vUEAdthUz7EBaEoYU3IYOUABMgrdBhQxJiRuFTPhqSEcRMA4WR6AcGYnCCQJnjeH4gTQOwEowAAMhA0RJAEaQZFkxZ5IUUIQdUdRNK0BjqAkaCMVGoyzO8nwcMCbIUb6SHgrcJkoGZ+gfEsVlHHZZyoSIKjlAg8mUkickKViOKxKuxrHgW5pnhuqi2igDpOo5EUyLFH5bv6MBBpw8YHvIEYwI5sbMAAchAeVhtoMAAGa+EJR4+RORY5GAvLlpW1a1uM0qytA5S0vSyBMo5XYalqCbhnVPgNWu6XeiBeYwgO8EIORTW+uO5RDYycgoFULkWUi5lLLMk3aASRLMINeqXnCHDmEgRIaI1JSLdRbWlk4ACMnXet1OENv1RXBM+0BIAAXigyzqj2Oq3VeD0YE9mrrfmE4xZacWzvOKD-g+HGvu682bl+WW-j+z6Aa9J4XJ58rBZSsHwVgIL04tmnykxiajKxeHPlx6CsWRr1eS1Jb0U4jEDJhLE8S87EC0RQvy6s6pCfxwmmF4vj+AEXgoOgsnyb4zBKekmSYB9m0Uby0hSjJUr1FKzQtPpFKJN0HGC2gaNeaBVz2TA3vK77bOB-7S2+TA-n2KbQUm3eoW4qlwHeej216nt+OPkrHaXXGN3DcHz4wJAHZo7TVFqZ9-I-X9YoSj19Z9fKM0ceDUMw92Wq6sXHFl6HlfRWlWMfuUD3cDez65wRoepe+C1k+UkTDBANCUwBh5zWPS-p8taCrcPzUB+B8qHwga3h2fHO2ytl9+5R8A13RDG9OrAma6JuuBPdskIjAAA4kmH05sVJWxrjbU48oKiAKdq7ewSYvZ51HNZaBtkI7yhDhXa+yEoEZxjgiYBopZ6cXnsffBy0doGjxjPbB6AC7XTpLdAe5dUGRQ2oWaurV2r1xgCKf6TdAat3KO3MGUBIbQzGnDPujIS73kHjgtB6NfSLwvHaHO9C0ALxJp+Qo34cp-iptVHQYQtFvl0bfJqO5jGGhpiPNR8UYB2mIWoGSCIkQ6N3qTfRWVAGJSASA-+sQLHeM-PvaOrjVDuJCfYk+oJkLlCiTE1mxx2ZcLzOhKYiDRQ9QqP0HJKBqTSB6t9WiZY+SvGUguWWvMJi-B0AgUAmY+wsXqa8QppUkz1L2DARo7lMlPw+q-KWvRskgLyQUpMxTSnlMqVMapKBWl1L6A0ppIAWm1KeKsjpSYumjB6X0j+Wtv7iQCNgHwUBsDcHgLjQJowUgW1Uq1fBWlagNAQUg0Gc8OyMU6UmAZnMMFnybCgtAsx-kxlweCSh0crx7SRHAXGrjk7hRKjAMkBkqSUmoc4pMzI7HKKrs-HhX0hT8KrIIusQN5SKhgMqMKYBpG91ugaYAFCIkoEzsNbOdCwWMJgEXORrCh5xPei-L6v0KVdSEb1RsIMO4SK7sywwsimQiqURwlRp4wkJTAJo-loTPQZWXtlYMm8XwmLMWCo155wkaSilyi11MiUON0Tja8KBXGkJ9hCvZSYvHGqXr4nce57liGJmEqxBCL5X1dfE+m5QkWepRWoFmiFMHRqyX0QpMzyhlIqTAQFNkMnDJgJLRi4zRh5pgAWvkRataay-jrM5lgNT+WSDAAAUhASk4bAiNOaRAl56c3n2l0i0QpyCfnoEYtc4AbaoBwAgP5KAfrq3SGLegummDQUzvBTAedi7l2rvXUU6QGab6cvKAAK17Wgb1WizJNOPSu6AZ7ikCsxRSbFzDi6FIJeysVpaJV13JQIxu1KREKjTPSlAKpYgqvhsXNlHKHXWJgNQ3l94fXkKuoKv9wrS5sLDvG8VpK65SogzWWVLd5ViPvJ3KRsMWX92I6KsjOqg3qMSga-dga7WZUDOa-G4ZTHyLIZq0e3H7VRydaJ7eWqTyYxk3q71ubpACbikJyCFhUAb1cWJsIGnbXaYyVtcNgF8OuODggVM6YYB3spNDQq7AhypDOEe2gwHxY0S+hWaVVLm40qbHZlsDmRydhY4YGzEBsDsLk6o91wc9xVBfZQQi6npnSGfQuqq8gtMmpDfGbANDLPVWSBkVIh70tQBdUcRx0blppby1AX80MOU7pBd2+9qa4IIWhWLdDJRyixsfuRiWb8ejHKbRrISpy9ZeAXc-FssBgDYGuYQeIiRHngOtqOmB9tHbO1dsYcbXXEkrbwB6PQBhUWpI8pm69l5uB4ERa9ur2hbsoHu6nR1SXdUvdW4uQrwa-Qrw2OvZMl8ir5SA5xlTdrZwfZzqDnx4PIJrw3kSBAEmfYvQR9JpHQO8BRM8aZorGPV5Q9s7jqJnXT6XcQKtm7+gftpoG2kp7B3RtH1FkMiVFb36NqEkAA)


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
