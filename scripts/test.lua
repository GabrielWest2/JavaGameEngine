local engine = luajava.bindClass("engine.GameEngine")
local time = luajava.bindClass("engine.util.Time")
local sceneManager = luajava.bindClass("engine.scene.SceneManager")
local mouse = luajava.bindClass("engine.input.Mouse")

function Start()
    print("LUA SCRIPT STARTED!")
end

function Update()
    if mouse:isMousePressed(0) then
        print("[LUA] hi")
        if sceneManager.loadedScene:getEntities():size() > 1 then
            entity = sceneManager.loadedScene:getEntities():get(0)
            print("[LUA] entity name: " .. entity:getName())
            entity:getTransform():getPosition().y = 1 * time:getDeltaTime() + entity:getTransform():getPosition().y;
        end
    end
end
