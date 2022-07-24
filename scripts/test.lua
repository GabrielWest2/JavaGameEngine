local engine = luajava.bindClass("engine.GameEngine"):getInstance()
local time = luajava.bindClass("engine.util.Time")

function Update()
    if engine.entityList:size() > 0 then
        entity = engine.entityList:get(0)
        print(time:getDeltaTime())
        entity:getTransform():getPosition().y =  15--entity:getTransform():getPosition().y + time:getDeltaTime()
    end
end

function LeftClick()
    --if engine.entityList:size() > 0 then
    --    engine.entityList:get(0):destroy()
    --end
end
