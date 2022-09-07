package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.screens.PhysBody;
import java.util.Iterator;

public class PhysX {
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    public final float PPM = 100;
    public MyContList mc;

    public PhysX() {
        world = new World(new Vector2(0, -9.81f), true);
        mc = new MyContList();
        world.setContactListener(mc);
        debugRenderer = new Box2DDebugRenderer();
    }

    public void destroyBody(Body body){world.destroyBody(body);}

    public Body addObject(RectangleMapObject object) {
        Rectangle rect = object.getRectangle();
        String type = (String) object.getProperties().get("BodyType");
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        CircleShape circleShape;
        ChainShape chainShape;

        if (type.equals("StaticBody")) def.type = BodyDef.BodyType.StaticBody;
        if (type.equals("DynamicBody")) def.type = BodyDef.BodyType.DynamicBody;

        def.position.set((rect.x + rect.width/2)/PPM, (rect.y + rect.height/2)/PPM);
        def.gravityScale = (float) object.getProperties().get("gravityScale");

        polygonShape.setAsBox(rect.width/2/PPM, rect.height/2/PPM);

        fdef.shape = polygonShape;
        fdef.friction = (float) object.getProperties().get("friction");
        fdef.density = 1;
        fdef.restitution = (float) object.getProperties().get("restitution");

        String name = "";
        if (object.getName() != null) name = object.getName();
        Body body;
        body = world.createBody(def);
        body.setUserData(new PhysBody(name, new Vector2(rect.x, rect.y), new Vector2(rect.width, rect.height)));
        body.createFixture(fdef).setUserData(name);

        if (name != null && name.equals("hero")) {
            polygonShape.setAsBox(rect.width/3/PPM, rect.height/12/PPM, new Vector2(0,-rect.width/2/PPM), 0);
            fdef.restitution = 0;
            body.createFixture(fdef).setUserData("sensor");
            body.getFixtureList().get(body.getFixtureList().size-1).setSensor(true);
            body.setFixedRotation(true);
            body.getFixtureList().get(0).setRestitution(0);

        }

        polygonShape.dispose();
        return body;
    }

    public Array<Body> getBodys(String name){
        Array<Body> ab = new Array<>();
        world.getBodies(ab);
        Iterator<Body> it = ab.iterator();
        while (it.hasNext()){
            String text = ((PhysBody)it.next().getUserData()).name;
            if (!text.equals(name)) it.remove();
        }
        return ab;
    }
    public void setGravity(Vector2 gravity) {world.setGravity(gravity);}
    public void step() {world.step(1/60.0f, 3, 3);}
    public void debugDraw(OrthographicCamera cam){debugRenderer.render(world, cam.combined);}

    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
}
