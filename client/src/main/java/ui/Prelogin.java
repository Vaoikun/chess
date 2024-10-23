package ui;

import httpresponse.LoginResponse;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.BLACK_KING;

public class Prelogin
{
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final Scanner SCANNER = new Scanner(System.in);

    public Prelogin(String serverUrl)
    {
        ServerFacade serverFacade = new ServerFacade(serverUrl);
    }

    public void run()
    {
        OUT.println("Welcome to the Chess Game. Type Here to get started.");
        OUT.println();
        OUT.println(help());
        OUT.println("Make your choice.");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "QUIT"))
        {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }


    public void eval(String input)
    {
            switch (input)
            {
                case "Register" -> register();
                case "Login" -> login();
                case "Help" -> OUT.println(help());
                case "Quit" -> quit();
                default -> OUT.println(help());
            }

    }

    public static void register() {
        OUT.println("Please set your username: ");
        String username = SCANNER.nextLine();
        OUT.println("Please set your password: ");
        String password = SCANNER.nextLine();
        OUT.println("Please set your email");
        String email = SCANNER.nextLine();

        try {
            Object registerReturn = ServerFacade.register(username, password, email);
            if (registerReturn instanceof RegisterResponse) {
                OUT.println("You successfully register the account.");
                RegisterResponse registerResponseReturned = (RegisterResponse) registerReturn;
                String authToken = registerResponseReturned.authToken();
                OUT.println();
                PostLogin postlogin = new PostLogin("http://localhost:8080", authToken);
                postlogin.run();
            } else {
                MessageResponse messageResponseRegister = (MessageResponse) registerReturn;
                OUT.println(messageResponseRegister.message());
                OUT.println(help());
            }
        } catch (IOException e) {
           OUT.println(e.getMessage());
        }
    }

    public static void login()
    {
        OUT.println("Please type your username.");
        String username = SCANNER.nextLine();
        OUT.println("Please type your password");
        String password = SCANNER.nextLine();

        try
        {
            Object loginReturn = ServerFacade.login(username, password);
            if (loginReturn instanceof LoginResponse)
            {
                LoginResponse loginResponseReturn = (LoginResponse)loginReturn;
                String authToken = loginResponseReturn.authToken();
                OUT.println("You successfully login the account.");
                // turn to postLogin. do this later
                PostLogin postLogin = new PostLogin("http://localhost:8080", authToken);
                postLogin.run();
            }
            else
            {
                MessageResponse messageResponse = (MessageResponse) loginReturn;
                OUT.println(messageResponse.message());
            }
            OUT.println(help());
        }
        catch (IOException E)
        {
            OUT.println(E.getMessage());
        }
    }

    public static String help()
    {
        return """
                Register <USERNAME> <PASSWORD> <EMAIL> -- To create an account
                Login <USERNAME> <PASSWORD> -- To play chess game.
                Help -- with possible commands.
                Quit -- Exits your chess game.
                """;
    }

    public void quit()
    {
        OUT.println("Your game is exit."); // the print is not showing up in console.
        System.exit(0);
    }



}
