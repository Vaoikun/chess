package httpresponse;

import model.GameData;

import java.util.ArrayList;

public record ListGameResponse(ArrayList<GameData> gameList) {
}
