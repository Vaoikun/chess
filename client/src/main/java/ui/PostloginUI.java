package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import httpresponse.CreateGameResponse;
import httpresponse.ListGameResponse;
import httpresponse.MessageResponse;
import model.GameData;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostloginUI {
    private final String authToken;
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);

    public static ArrayList<Integer> gameIDList = new ArrayList<>();

    public PostloginUI (String serverURL, String authToken) {
         ServerFacade serverFacade = new ServerFacade(serverURL);
         this.authToken = authToken;
    }

    public void run() {
        OUT.println();
        OUT.println("Welcome to the Chess server!");
        OUT.println();
        OUT.println(help());
        OUT.println();
        OUT.println("Enter command below.");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")) {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void createGame() {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        OUT.println("Set a name for the game below.");
        String gameName = SCANNER.nextLine();
        try {
            Object createGameResult = ServerFacade.createGame(gameName, authToken);
            Object listGameResult = ServerFacade.listGames(authToken);
            ListGameResponse listGameResponse = (ListGameResponse) listGameResult;
            ArrayList<GameData> gameList = listGameResponse.games();
            for (GameData gameData : gameList) {
                if (!gameIDList.contains(gameData.gameID())) {
                    gameIDList.add(gameData.gameID());
                }
            }
            if (createGameResult instanceof CreateGameResponse createGameResponse) {
                int gameID = createGameResponse.gameID();
                Collections.sort(gameIDList);
                OUT.println("Game creation successful.");
                OUT.println("gameID: " + (gameIDList.indexOf(gameID) + 1));
            } else {
                MessageResponse messageResponse = (MessageResponse) createGameResult;
                OUT.println(messageResponse.message());
            }
            OUT.println();
            OUT.println("Enter command below.");
        } catch (Exception e) {
            OUT.println(e.getMessage());
        }
    }

    public void joinGame () {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        Gson json = new Gson();
        OUT.println("Enter the gameID below.");
        String userGameID = SCANNER.nextLine();
        try {
            int gameID = Integer.parseInt(userGameID);
            OUT.println("Chose your team. (White/Black)");
            String teamColor = SCANNER.nextLine();
            if (!Objects.equals(teamColor, "White") && !teamColor.equals("Black")) {
                OUT.println("Must enter White or Black.");
            } else {
                ChessGame.TeamColor chosenColor = json.fromJson(teamColor, ChessGame.TeamColor.class);
                join(chosenColor, gameID);
            }
        } catch (Exception e) {
            OUT.println("Enter a valid number.");
        }
    }

    public void join (ChessGame.TeamColor chosenColor, int gameID) {
        try {
            MessageResponse messageResponse = ServerFacade.joinGame(gameIDList.get(gameID - 1), chosenColor, authToken);
            if (!Objects.equals(messageResponse, "")) {
                OUT.println(messageResponse.message());
            } else {
                GameplayUI gameplayUI = new GameplayUI("http://localhost:8080", authToken, chosenColor, gameIDList.get(gameID - 1));
                OUT.println("Joining the game...");
                gameplayUI.run();
                OUT.println(RESET_BG_COLOR);
                OUT.println(RESET_TEXT_COLOR);
            }
        } catch (Exception e) {
            OUT.println("Error: Check the game list.");
        }
    }

    public void listGames () {
        OUT.println(RESET_TEXT_COLOR);
        OUT.println(RESET_BG_COLOR);
        try {
            Object listGameResult = ServerFacade.listGames(authToken);
            if (listGameResult instanceof ListGameResponse listGameResponse) {
                ArrayList<GameData> gameList = listGameResponse.games();
                if (gameList.isEmpty()) {
                    OUT.println("No games in the server. Please create one.");
                } else {
                    listOutput(gameList);
                }
            } else {
                MessageResponse messageResponse = (MessageResponse) listGameResult;
                OUT.println(messageResponse.message());
            }
            OUT.println("Enter command below.");
        } catch (Exception e) {
            OUT.println(e.getMessage());
        }
    }

    public void listOutput (ArrayList<GameData> gameList) {
        for (GameData game : gameList) {
            if (!gameIDList.contains(game.gameID())) {
                gameIDList.add(game.gameID());
            }
            String output = "Game name: " + game.gameName() + "  gameID: " + gameIDList.indexOf(game.gameID() + 1)
                    + "  White team: " + game.whiteUsername() + "  Black team: " + game.blackUsername();
            OUT.println(output);
            OUT.println();
        }
    }

    public void observeGame () {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        try {
            Object listGameResult = ServerFacade.listGames(authToken);
            ListGameResponse listGameResponse = (ListGameResponse) listGameResult;
            ArrayList<GameData> gameList = listGameResponse.games();
            if (gameList.isEmpty()) {
                OUT.println("No games in the server. Please create one.");
            } else {
                OUT.println("Enter a gameID below.");
                String inputGameID = SCANNER.nextLine();
                try {
                    int gameID = Integer.parseInt(inputGameID);
                    GameplayUI gameplayUI = new GameplayUI("http://localhost:8080", authToken,
                            null, gameIDList.indexOf(gameID - 1));
                    OUT.println("Observing the game...");
                    gameplayUI.run();
                } catch (Exception e) {
                    OUT.println("Invalid gameID.");
            }
        }
            OUT.println(RESET_BG_COLOR);
            OUT.println(RESET_TEXT_COLOR);
        } catch (IOException e) {
            OUT.println(e.getMessage());
        }
    }

    public void logout () {
        try {
            MessageResponse messageResponse = ServerFacade.logout(authToken);
            if (!Objects.equals(messageResponse.message(), "")) {
                OUT.println(messageResponse.message());
            } else {
                OUT.println("Logging out...");
                PreloginUI preloginUI = new PreloginUI("http://localhost:8080");
                preloginUI.run();
            }
        } catch (IOException e) {
            OUT.println(e.getMessage());
            OUT.println("Enter command below.");
        }
    }

    public static String help() {
        return """
                Create Game <Name>: Create a game.
                Join Game <GameID>: Join a game.
                List Games: List games.
                Observe <GameID>: Observe a game.
                Logout: Logout.
                Help: Show commands.
                """;
    }

    public void eval(String input) {
        switch (input) {
            case "Help" -> OUT.println(help());
            case "Create Game" -> createGame();
            case "Join Game" -> joinGame();
            case "List Games" -> listGames();
            case "Observe" -> observeGame();
            case "Logout" -> logout();
            default -> OUT.println(help());
        }
    }

}
