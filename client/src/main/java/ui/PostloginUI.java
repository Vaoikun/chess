package ui;


import chess.ChessGame;
import com.google.gson.Gson;
import httpresult.CreateGameResult;
import httpresult.ListGameResult;
import httpresult.MessageResult;
import model.GameData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostloginUI {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);

    private PreloginUI preLogin;
    private final String authToken;
    public static ArrayList<Integer> gamesNumber = new ArrayList<>();

    private final WebSocketFacade webSocketFacade = new WebSocketFacade("http://localhost:8080", ChessGame.TeamColor.WHITE, null);

    public PostloginUI(String serverUrl, String authToken){
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        this.authToken = authToken;
    }

    public void run(){
        OUT.println();
        OUT.println("Welcome to the server!");
        OUT.println();
        OUT.println("Type Help for more commands.");
        OUT.println();
        OUT.println("What would you like to do?");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")){
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void eval(String input){
        switch (input){
            case "Help" -> OUT.println(help());
            case "Create a Game" -> createGame();
            case "Play a Game" -> playGame();
            case "List Games" -> listGames();
            case "Observe" -> observeGame();
            case "Log out" -> logOut();
            case "Quit" -> quit();
            default -> OUT.println("What would you like to do?");
        }
    }

    public static String help()
    {
        return """
               Create a Game <Name> -- Create a new chess game.
               Play a Game <GameID> -- Join a chess game.
               List Games -- List all the games.
               Observe <GameID> -- Observe a game.
               Log out -- Logout your account.
               Quit -- Exit your chess game.
               Help - With possible commands.
               
               What would you like to do?
               """;
    }

    public void createGame(){
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        OUT.println("Give a name for the game.");
        String gameName = SCANNER.nextLine();
        try {
            Object createGameReturn = ServerFacade.createGame(gameName, authToken);
            Object listGameObject = ServerFacade.listGame(authToken);
            ListGameResult listGameResult = (ListGameResult) listGameObject;
            ArrayList<GameData> listGames = listGameResult.games();
            for(GameData gameData : listGames){
                if (!gamesNumber.contains(gameData.gameID())){
                    gamesNumber.add(gameData.gameID());
                }
            }

            if (createGameReturn instanceof CreateGameResult){
                CreateGameResult createGameResultReturn = (CreateGameResult) createGameReturn;
                gamesNumber.add(createGameResultReturn.gameID());
                OUT.println("You have successfully created a new game. Game ID is: " + createGameResultReturn.gameID());
            }else{
                MessageResult messageResult = (MessageResult) createGameReturn;
                OUT.println(messageResult.message());
            }
            OUT.println();
            OUT.println("What would you like to do?");
        }catch (Exception e) {
            OUT.println(e.getMessage());
        }
    }

    public void playGame() {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        Gson gson = new Gson();
        OUT.println("Enter the gameID of the game you would like to join.");
        String gameIdStr = SCANNER.nextLine();
        try {
            int gameID = Integer.parseInt(gameIdStr);
            OUT.println("Choose your team color.");
            String playerColor = SCANNER.nextLine();
            if (!Objects.equals(playerColor, "WHITE") && !playerColor.equals("BLACK")) {
                OUT.println("Bad Request. The color must be BLACK or WHITE.");
            } else {
                ChessGame.TeamColor playerColorChanged = gson.fromJson(playerColor, ChessGame.TeamColor.class);
                joinGame(playerColorChanged, gameID);
            }
        }catch (Exception e) {
            OUT.println("Must enter a valid number.");
        }
    }

    private void joinGame(ChessGame.TeamColor playerColorChanged, int gameID) {
        try {
            MessageResult messageResponseJoinGame = ServerFacade.joinGame(playerColorChanged, gamesNumber.get(gameID - 1), authToken);
            if (!Objects.equals(messageResponseJoinGame.message(), "")) {
                OUT.println(messageResponseJoinGame.message());
            } else {
                OUT.println("You successfully joined the game");
                if (playerColorChanged == ChessGame.TeamColor.BLACK) {
                    webSocketFacade.setColor(ChessGame.TeamColor.BLACK);
                    webSocketFacade.connectPlayer(authToken, gamesNumber.get(gameID - 1));
                }else{
                    webSocketFacade.setColor(ChessGame.TeamColor.WHITE);
                    webSocketFacade.connectPlayer(authToken, gamesNumber.get(gameID - 1));
                }
                GameplayUI gamePlayUI = new GameplayUI("http://localhost:8080", authToken,
                        webSocketFacade, playerColorChanged, gamesNumber.get(gameID - 1));
                gamePlayUI.run();
                OUT.println(RESET_BG_COLOR);
                OUT.println(RESET_TEXT_COLOR);
            }
        } catch (Exception e) {
            OUT.println("Error Joining Game. Please check the game list.");
        }
    }

    public void listGames() {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        try {
            Object listGameReturn = ServerFacade.listGame(authToken);
            if (listGameReturn instanceof ListGameResult lIstGameResponseReturn) {
                ArrayList<GameData> listGames = lIstGameResponseReturn.games();
                if (listGames.isEmpty()) {
                    OUT.println("No games in server");
                }else{
                    gameLister(listGames);
                }
            }else{
                MessageResult messageResponse = (MessageResult) listGameReturn;
                OUT.println(messageResponse.message());
            }
            OUT.println("What would you like to do?");
        }catch (Exception E){
            OUT.println(E.getMessage());
        }
    }

    public void gameLister(ArrayList<GameData> listGames){
        for (GameData listG : listGames) {
            if (!gamesNumber.contains(listG.gameID())) {
                gamesNumber.add(listG.gameID());
            }
            String listGameStr = "Game Name: " + listG.gameName() + ". Game number: " + listG.gameID()
                    + ". White user: " + listG.whiteUsername() + ". Black user: " + listG.blackUsername();
            OUT.println(listGameStr);
            OUT.println();
            OUT.println();
        }
    }

    public void observeGame() {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        try {
            Object listGameObj = ServerFacade.listGame(authToken);
            ListGameResult listGameResult = (ListGameResult) listGameObj;
            ArrayList<GameData> listGames = listGameResult.games();
            if (listGames.isEmpty()) {
                OUT.println("No games in server.");
            } else {
                System.err.println();
                OUT.println("Which game you would like to observe.");
                String gameIdStr = SCANNER.nextLine();
                try {
                    int gameID = Integer.parseInt(gameIdStr);
                    webSocketFacade.setColor(ChessGame.TeamColor.WHITE);
                    webSocketFacade.connectPlayer(authToken, gamesNumber.get(gameID - 1));
                    GameplayUI gamePlayUI = new GameplayUI("http://localhost:8080", authToken, webSocketFacade,
                            null, gamesNumber.get(gameID - 1));
                    gamePlayUI.run();
                    OUT.println("You are observing the game");
                } catch (Exception e) {
                    OUT.println("Must enter a valid number.");
                }
            }
            OUT.println(RESET_BG_COLOR);
            OUT.println(RESET_TEXT_COLOR);
        } catch(IOException e) {
            OUT.println(e.getMessage());
        }
    }

    public void logOut() {
        try {
            MessageResult messageResponseLogOut = ServerFacade.logout(authToken);
            if (!Objects.equals(messageResponseLogOut.message(), "")) {
                OUT.println(messageResponseLogOut.message());
            } else {
                OUT.println("You successfully logged out of the game.");
                PreloginUI prelogin1 = new PreloginUI("http://localhost:8080");
                prelogin1.run();
            }
        } catch (IOException e) {
            OUT.println(e.getMessage());
            PreloginUI prelogin1 = new PreloginUI("http://localhost:8080");
            prelogin1.run();
        }
    }

    public void quit() {
        OUT.println("You exited the game.");
        System.exit(0);
    }
}
