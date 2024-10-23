package httpresponse;

import model.GameData;

import java.util.ArrayList;

public record LIstGameResponse(ArrayList<GameData> games) {
}
