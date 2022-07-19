package engine.postprocessing;

import engine.shader.ShaderProgram;

public abstract class PostProcessingEffect extends ShaderProgram {
    public boolean enabled = true;

    public PostProcessingEffect(String name) {
        super("src/main/java/engine/postprocessing/" + name + "/src/vertex.shader", "src/main/java/engine/postprocessing/" + name + "/src/fragment.shader");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }


    public abstract void render(int buff);
}
