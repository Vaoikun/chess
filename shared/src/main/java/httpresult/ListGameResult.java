package httpresult;

import model.GameData;

import java.util.ArrayList;

public record ListGameResult(ArrayList<GameData> gameList) {
}
