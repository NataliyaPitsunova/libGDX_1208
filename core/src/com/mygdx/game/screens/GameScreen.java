package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.*;
import com.mygdx.game.persons.Actions;
import com.mygdx.game.persons.Man;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture img;
    private final Man man;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    //private final ShapeRenderer shapeRenderer;
    private PhysX physX;
    private final int[] bg;
    private final int[] l1;
    private Body body;
    public static ArrayList<Body> bodies;
    public static ArrayList<Balls> balls;
    private final Anim animation;
    private  final RectangleMapObject bullet;
    private final NewFont font;
    private int score;
    private  int maxScore;


    public GameScreen(Main game, String mapName) {
        font = new NewFont(30);
        font.setColor(Color.BLACK);

        man = new Man();
        animation = new Anim("atlas/roll.atlas", "roll", Animation.PlayMode.LOOP);
        bodies = new ArrayList<>();
        balls = new ArrayList<>();
        this.game = game;
        batch = new SpriteBatch();
        //shapeRenderer = new ShapeRenderer();
        img = new Texture("fon.jpg");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.25f;

        TiledMap map = new TmxMapLoader().load(mapName); //!!!!!
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        bg = new int[1];
        bg[0] = map.getLayers().getIndex("фон");
        l1 = new int[2];
        l1[0] = map.getLayers().getIndex("Слой 2");
        l1[1] = map.getLayers().getIndex("Слой 3");

        physX = new PhysX();

        map.getLayers().get("объекты").getObjects().getByType(RectangleMapObject.class); //выбор объектов по типу
        RectangleMapObject tmp = (RectangleMapObject) map.getLayers().get("сеттинг").getObjects().get("hero"); //выбор объекта по имени
        body = physX.addObject(tmp);

        bullet = (RectangleMapObject) map.getLayers().get("сеттинг").getObjects().get("hero");

        Array<RectangleMapObject> objects = map.getLayers().get("объекты").getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        maxScore = physX.getBodys("roll").size;

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && physX.mc.isOnGround()) body.applyForceToCenter (new Vector2(-0.65f, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && physX.mc.isOnGround()) body.applyForceToCenter (new Vector2(0.65f, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && physX.mc.isOnGround()) body.applyForceToCenter (new Vector2(0, 4f), true);

        if (Gdx.input.isButtonPressed(Input.Keys.SPACE) && physX.mc.isOnGround()) {
            body.setLinearVelocity(0, 0);
        }

        man.setFPS(body.getLinearVelocity(), physX.mc.isOnGround());


        if (Gdx.input.isKeyPressed(Input.Keys.P)) camera.zoom += 0.01f;
        if (Gdx.input.isKeyPressed(Input.Keys.O) && camera.zoom > 0) camera.zoom -= 0.01f;

        camera.position.x = body.getPosition().x * physX.PPM;
        camera.position.y = body.getPosition().y * physX.PPM;
        camera.update();

        ScreenUtils.clear(Color.BLACK);

        batch.begin();
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render(bg);

        Rectangle tmp = man.getRect(camera, man.getFrame());
        ((PolygonShape)body.getFixtureList().get(0).getShape()).setAsBox(tmp.width/2/physX.PPM*camera.zoom, tmp.height/2/ physX.PPM*camera.zoom);
        ((PolygonShape)body.getFixtureList().get(1).getShape()).setAsBox(
                tmp.width/3/physX.PPM*camera.zoom,
                tmp.height/12/physX.PPM*camera.zoom,
                new Vector2(0,-tmp.height/2/physX.PPM*camera.zoom),
                0);

        man.setTime(Gdx.graphics.getDeltaTime());
        animation.setTime(Gdx.graphics.getDeltaTime());

        batch.begin();
        batch.draw(man.getFrame(), tmp.x,tmp.y, tmp.width, tmp.height);
        batch.end();

        TextureRegion imgT = animation.getFrame();
        batch.begin();
        Array<Body> ab = physX.getBodys("roll");
        for (Body b: ab) {
            float x = Gdx.graphics.getWidth()/2 + (b.getPosition().x * physX.PPM - ((PhysBody) b.getUserData()).size.x/2 - camera.position.x) / camera.zoom;
            float y = Gdx.graphics.getHeight()/2 + (b.getPosition().y * physX.PPM - ((PhysBody) b.getUserData()).size.y/2 - camera.position.y) / camera.zoom;
            batch.draw(imgT, x, y,
                    ((PhysBody) b.getUserData()).size.x / camera.zoom,
                    ((PhysBody) b.getUserData()).size.y / camera.zoom);
        }
        batch.end();

        mapRenderer.render(l1);

        batch.begin();
        font.render(batch, "Шариков собрано: " + String.valueOf(score), 0, Gdx.graphics.getHeight());
        batch.end();

        physX.step();
        physX.debugDraw(camera);

        for (int i = 0; i < bodies.size(); i++) {
            physX.destroyBody(bodies.get(i));
            score++;
        }
        bodies.clear();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }

        if (score == maxScore){
            dispose();
            game.setScreen(new GameScreen(game, "map/карта1.tmx"));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.img.dispose();
        this.physX.dispose();
        this.man.dispose();
        this.font.dispose();
        this.mapRenderer.dispose();
        this.animation.dispose();
    }
}
