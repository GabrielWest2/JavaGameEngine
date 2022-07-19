package scripting;

public class GameEngineAPI {

    private static GameEngineAPI instance;

    public GameEngineAPI() {
        instance = this;
    }

    public static GameEngineAPI getInstance() {
        return instance;
    }

    public void Update() {

    }


}
