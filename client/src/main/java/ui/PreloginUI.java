package ui;

public class PreloginUI {

    public PreloginUI(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
    }

    public void run() {

    }

    public static void register() {

    }

    public static void login() {

    }

    public void quit() {

    }


    public static String help() {
        return """
                   To create an account: Register (USERNAME) (PASSWORD) (EMAIL)
                   To login into an account: Login (USERNAME) (PASSWORD)
                   To exit the server: Quit
                   To show commands: Help
                   """;
    }
}
