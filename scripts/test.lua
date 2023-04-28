local engine = luajava.bindClass("engine.GameEngine"):getInstance()
local time = luajava.bindClass("engine.util.Time")
local textureLoader = luajava.bindClass("engine.texture.TextureLoader")
local objLoader = luajava.bindClass("engine.model.OBJLoader")
local modelRenderer = luajava.bindClass("engine.ecs.component.ModelRenderer")

function Update()
    --if engine.loadedScene:getEntities():size() == 1 then
    --    tex = textureLoader:getTexture("grass_color.png")
    --    model = objLoader:loadTexturedOBJ("dragon.obj", tex);


    --    entity = engine.loadedScene:getEntities():get(0)

    --    entity:getTransform():getPosition().y = entity:getTransform():getPosition().y + time:getDeltaTime()
    --end
end

function LeftClick()
        if engine.loadedScene:getEntities():size() > 1 then
            tex = textureLoader:getTexture("white.png")
            model = objLoader:loadTexturedOBJ("dragon.obj", tex);

            entity = engine.loadedScene:getEntities():get(1)
            rendererClass = modelRenderer:clazz()
            renderer = entity:getComponent(rendererClass)
            renderer:setModel(model)
            --entity:getTransform():getPosition().y = entity:getTransform():getPosition().y + time:getDeltaTime()
        end
end
