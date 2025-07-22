package dataaccess;

import model.GameData;

import java.util.HashSet;

public class GameMDAO implements GameDAO{

    private static final HashSet<GameData> GAME_DATA_HASH_SET = new HashSet<>();

    @Override
    public void clear() throws DataAccessException{
        GAME_DATA_HASH_SET.clear();
    }
}
