package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class SQLGame implements GameDAO
{
    private static final String CREATE_STATEMENT =

                    //the primary key is gameID I guess?
                    """
                    CREATE TABLE IF NOT EXISTS Games
                    (
                        gameIDCol INT NOT NULL,
                        whiteUserNameCol varchar(255),
                        blackUserNameCol varchar(255),
                        gameNameCol varchar(255) NOT NULL,
                        ChessGameCol TEXT NOT NULL,
                         PRIMARY KEY (gameIDCol)
                    )
                    """
            ;
    public SQLGame() throws DataAccessException {
        createGamesTable();
    }

    public static void createGamesTable() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement(CREATE_STATEMENT))
            {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException
    {
        if (gameName == null)
        {
            throw new DataAccessException("Game name is null");
        }
        Gson gson = new Gson();
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO Games(gameIDCol, whiteUserNameCol, blackUserNameCol, gameNameCol, ChessGameCol) VALUES (?,?,?,?,?);"))
            {
                ChessGame newGame = new ChessGame();
                String jsonGame = gson.toJson(newGame);
                Random random = new Random();
                int randomInt = random.nextInt(10000); // get the random number between 0 - 10000
                preparedStatement.setInt(1, randomInt);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, jsonGame);

                preparedStatement.executeUpdate();
                return randomInt;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String whiteUserName, blackUserName, gameName, chessGame;
        Gson gson = new Gson();
        GameData getGameData;
       try (var conn = DatabaseManager.getConnection())
       {
           try (var preparedStatement = conn.prepareStatement("SELECT gameIDCol, whiteUserNameCol, blackUserNameCol, gameNameCol, ChessGameCol FROM Games WHERE gameIDCol = ?;"))
           {
               preparedStatement.setInt(1, gameID);
               try (var rs = preparedStatement.executeQuery())
               {
                   if (rs.next())
                   {
                       whiteUserName = rs.getString("whiteUserNameCol");
                       blackUserName = rs.getString("blackUserNameCol");
                       gameName = rs.getString("gameNameCol");
                       chessGame = rs.getString("ChessGameCol");
                       ChessGame getGame = gson.fromJson(chessGame, chess.ChessGame.class);
                       getGameData = new GameData(gameID, whiteUserName, blackUserName, gameName, getGame);
                       return getGameData;
                   }
                   else
                   {
                       throw new DataAccessException("The game is not found.");
                   }
               } catch (SQLException e) {
                   throw new DataAccessException(e.getMessage());
               }
           }
       }
       catch (SQLException e)
       {
           throw new DataAccessException(e.getMessage());
       }
    }

    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException
    {
        if (authToken == null)
        {
            throw new DataAccessException("AuthToken is null");
        }
        String whiteUserName, blackUserName, gameName, chessGame;
        int gameID;
        Gson gson = new Gson();
        ArrayList<GameData> returnedGames =new ArrayList<>();
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM Games;")) {
                try (var rs = preparedStatement.executeQuery())
                {
                    while (rs.next()) // I think not if, because we need all gameData in db.
                    {
                        gameID = rs.getInt("gameIDCol");
                        whiteUserName = rs.getString("whiteUserNameCol");
                        blackUserName = rs.getString("blackUserNameCol");
                        gameName = rs.getString("gameNameCol");
                        chessGame = rs.getString("ChessGameCol");
                        ChessGame getGame = gson.fromJson(chessGame, chess.ChessGame.class);
                        returnedGames.add(new GameData(gameID, whiteUserName, blackUserName, gameName, getGame));
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
            catch (SQLException e)
            {
                throw new DataAccessException(e.getMessage());
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
        return returnedGames;
    }

    /**
     * @param username
     * @param playerColor
     * @param targetGame
     * @throws DataAccessException
     */
    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData targetGame) throws DataAccessException {
        int gameID = targetGame.gameID();
        try (var conn = DatabaseManager.getConnection())
        {
            if (playerColor == ChessGame.TeamColor.WHITE)
            {
                try (var preparedStatement = conn.prepareStatement("UPDATE Games SET whiteUserNameCol = ? WHERE gameIDCol = ?;"))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
            else
            {
                try (var preparedStatement = conn.prepareStatement("UPDATE Games SET blackUserNameCol = ? WHERE gameIDCol = ?;"))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
        }
        catch (SQLException E)
        {
            throw new DataAccessException(E.getMessage());
        }
    }

    public void updateChessGame(ChessGame chessGame, int gameID)
    {
        Gson gson = new Gson();
        String jsonChessGame = gson.toJson(chessGame);
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("UPDATE Games SET ChessGameCol = ? WHERE gameIDCol = ?"))
            {
                preparedStatement.setString(1, jsonChessGame);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @param gameID
     * @param playerColor
     * @param username
     * @throws DataAccessException
     */
    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException
    {
        if (username == null)
        {
            throw new DataAccessException("Your username is null");
        }
        GameData game = getGame(gameID);
        updateGame(username, playerColor, game);
    }

    /**
     * @throws DataAccessException
     */

    @Override
    public void clear() throws DataAccessException
    {
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE Games;"))
            {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }
}
