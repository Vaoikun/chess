package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import httpresponse.CreateGameResponse;
import httpresponse.LIstGameResponse;
import httpresponse.MessageResponse;
import model.GameData;
import org.junit.platform.commons.function.Try;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class PostLogin
{
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final Scanner SCANNER = new Scanner(System.in);

    private final WebSocketFacade webSocketFacade = new WebSocketFacade("http://localhost:8080", ChessGame.TeamColor.WHITE, null);
    public static ArrayList<Integer> gamesNumber = new ArrayList<>();
    private Prelogin prelogin;

    private final String authToken;
    public PostLogin(String serverUrl, String authToken)
    {
       ServerFacade serverfacade = new ServerFacade(serverUrl);
       this.authToken = authToken;
    }

    public void run()
    {
        OUT.println();
        OUT.println("Welcome to your chess game account. Please make your choice");

        OUT.println();
        OUT.println(help());
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit"))
        {
            this.eval(input);
            input = SCANNER.nextLine();
        }

    }

    public void eval(String input)
    {
        switch (input)
        {
            case "Create Game" -> createGame();
            case "List Games" -> listGame();
            case "Join Game" -> joinGame();
            case "Observe" -> observeGame();
            case "Log out" ->logOut();
            case "Quit" -> quit();
            case "Help" -> OUT.println(help());
            default -> OUT.println(help());
        }

    }
    public static String help()
    {
        return """
               Create Game <Name> -- Create a new chess game.
               List Games -- List all the games.
               Join Game <GameID> <UR PlayerColor> -- Join a current game.
               Observe <GameID> -- Observe a current game.
               Log out -- Logout your account.
               Quit -- Exits your chess game.
               Help - With possible commands.
                """;
    }

    public void quit()
    {
        OUT.println("Your game is exit."); // the print is not showing up in console.
        System.exit(0);
    }

    public void createGame() {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        OUT.println("Please type the game name you want to create.");
        String gameName = SCANNER.nextLine();
        int number = 1;
        try {
            Object createGameReturn = ServerFacade.createGame(gameName, authToken);
            Object listGameObj = ServerFacade.listGame(authToken);
            LIstGameResponse lIstGameResponse = (LIstGameResponse) listGameObj;
            ArrayList<GameData> listGames = lIstGameResponse.games();
            for (GameData listGame : listGames)
            {
                if (!gamesNumber.contains(listGame.gameID())) {
                    gamesNumber.add(listGame.gameID());
                }
            }
            if (createGameReturn instanceof CreateGameResponse)
            {
                CreateGameResponse createGameResponseReturn = (CreateGameResponse) createGameReturn;
                gamesNumber.add(createGameResponseReturn.gameID());
                Object listGameReturn = ServerFacade.listGame(authToken);
                LIstGameResponse lIstGameRes = (LIstGameResponse) listGameReturn;
                ArrayList<GameData> games = lIstGameRes.games();
                OUT.println("You successfully created a chess game. the game id is: " + listGames.size());

            }
            else
            {
                MessageResponse messageResponse = (MessageResponse) createGameReturn;
                OUT.println(messageResponse.message());
            }
        }
        catch(Exception E)
            {
                OUT.println(E.getMessage());
            }

    }

    public void listGame()
    {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);
        int length = 1;
        try
        {
            Object listGameReturn = ServerFacade.listGame(authToken);
            if (listGameReturn instanceof LIstGameResponse lIstGameResponseReturn)
            {
                ArrayList<GameData> listGames = lIstGameResponseReturn.games();
                if (listGames.isEmpty())
                {
                    OUT.println("No games in server");
                }
                else
                {
                    for (GameData listG : listGames) {
                        if (!gamesNumber.contains(listG.gameID())) {
                            gamesNumber.add(listG.gameID());
                        }
                        String listGameStr = "Game Name: " + listG.gameName() + ". Game number: " + length + ". White user: " + listG.whiteUsername() + ". Black user: " + listG.blackUsername();
                        OUT.println(listGameStr);
                        length++;
                        OUT.println();
                        OUT.println();
                    }
                }


            }
            else
            {
                MessageResponse messageResponse = (MessageResponse) listGameReturn;
                OUT.println(messageResponse.message());
            }
            OUT.println(help());
        }
        catch (Exception E)
        {
            OUT.println(E.getMessage());
        }
    }

    public void joinGame()
    {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        Gson gson = new Gson();
        OUT.println("Please tell me which game you would like to join.");
        String gameIdStr = SCANNER.nextLine();
        int gameID = Integer.parseInt(gameIdStr);
        OUT.println("Please tell me what color you would like to join");
        String playerColor = SCANNER.nextLine();
        if (!Objects.equals(playerColor, "WHITE") && !playerColor.equals("BLACK"))
        {
           OUT.println("BAD Request. Color should be all capital.");
        }
        else
        {
            ChessGame.TeamColor playerColorChanged = gson.fromJson(playerColor, ChessGame.TeamColor.class);
            try
            {
                MessageResponse messageResponseJoinGame = ServerFacade.joinGame(playerColorChanged,  gamesNumber.get(gameID-1), authToken);
                if (!Objects.equals(messageResponseJoinGame.message(), ""))
                {
                    OUT.println(messageResponseJoinGame.message());
                }
                else
                {
                    OUT.println("You successfully join the game");
                    if (playerColorChanged == ChessGame.TeamColor.BLACK)
                    {
                        webSocketFacade.setColor(ChessGame.TeamColor.BLACK);
                        webSocketFacade.connectPlayer(authToken, gamesNumber.get(gameID-1));
                        // go to gameUI
                    }
                    else
                    {
                        webSocketFacade.setColor(ChessGame.TeamColor.WHITE);
                        webSocketFacade.connectPlayer(authToken, gamesNumber.get(gameID-1));
                    }

                    GamePlayUI gamePlayUI = new GamePlayUI("http://localhost:8080", authToken, webSocketFacade, playerColorChanged,gamesNumber.get(gameID-1) );
                    gamePlayUI.run(); // go to the gamePlayUI
                    OUT.println(RESET_BG_COLOR);
                    OUT.println(RESET_TEXT_COLOR);
                }
            }
            catch (Exception e)
            {
                OUT.println(e.getMessage());
            }
        }
    }

    public void observeGame()
    {
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        try
        {
            Object listGameObj = ServerFacade.listGame(authToken);
            LIstGameResponse lIstGameResponse = (LIstGameResponse) listGameObj;
            ArrayList<GameData> listGames = lIstGameResponse.games();
            if (listGames.isEmpty())
            {
                OUT.println("No games in server.");
            }
            else
            {
                System.err.println();
                OUT.println("Please tell me which game you would like to observe.");
                String gameIdStr = SCANNER.nextLine();
                int gameID = Integer.parseInt(gameIdStr);
                OUT.println("You successfully observe the game");
                webSocketFacade.setColor(ChessGame.TeamColor.WHITE);
                webSocketFacade.connectPlayer(authToken,  gamesNumber.get(gameID-1));
                GamePlayUI gamePlayUI = new GamePlayUI("http://localhost:8080", authToken, webSocketFacade, null, gamesNumber.get(gameID-1) );
                gamePlayUI.run();
            }
            OUT.println(RESET_BG_COLOR);
            OUT.println(RESET_TEXT_COLOR);
        }
        catch(IOException e)
        {
            OUT.println(e.getMessage());
        }

    }

    public void logOut()
    {
        try
        {
           MessageResponse messageResponseLogOut = ServerFacade.logout(authToken);
           if (!Objects.equals(messageResponseLogOut.message(), "")) // error
           {
               OUT.println(messageResponseLogOut.message());
           }
           else
           {
               OUT.println("You successfully logout the game.");
               Prelogin prelogin1 = new Prelogin("http://localhost:8080");
               prelogin1.run();
           }
        }
        catch (IOException e)
        {
            OUT.println(e.getMessage());
            Prelogin prelogin1 = new Prelogin("http://localhost:8080");
            prelogin1.run();
        }
    }




}
