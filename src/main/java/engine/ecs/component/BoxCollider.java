package engine.ecs.component;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import engine.ecs.Component;
import org.joml.Vector3f;

public class BoxCollider extends Component {
    private Vector3f halfExtents = new Vector3f(0.5f);

    public BoxCollider(){

    }

    @Override
    public void onAdded(){
        Rigidbody3D rb = entity.getComponent(Rigidbody3D.class);
        if(rb == null) {
            rb = new Rigidbody3D();
            entity.addComponent(rb);
        }
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(halfExtents.x, halfExtents.y, halfExtents.z));
        shape.calculateLocalInertia(rb.mass, new javax.vecmath.Vector3f(0, 0, 0));
        rb.rb.setMassProps(rb.mass, new javax.vecmath.Vector3f());
        rb.rb.updateInertiaTensor();
        rb.rb.setCollisionShape(shape);
    }

    @Override
    public void onVariableChanged(){
        Rigidbody3D rb = entity.getComponent(Rigidbody3D.class);
        if(rb == null) {
            rb = new Rigidbody3D();
            entity.addComponent(rb);
        }
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(halfExtents.x, halfExtents.y, halfExtents.z));
        shape.calculateLocalInertia(rb.mass, new javax.vecmath.Vector3f(0, 0, 0));
        rb.rb.setMassProps(rb.mass, new javax.vecmath.Vector3f());
        rb.rb.updateInertiaTensor();
        rb.rb.setCollisionShape(shape);
    }
}
