package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Anim;
import com.mygdx.game.Main;
import com.mygdx.game.PhysX;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture img;
    //private final Anim animation;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    //private final ShapeRenderer shapeRenderer;
    private PhysX physX;
    private final int[] bg;
    private final int[] l1;
    private Body body;
    private final Rectangle heroRect;


    public GameScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        //shapeRenderer = new ShapeRenderer();
        img = new Texture("titleSpecter.png");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.25f;

        TiledMap map = new TmxMapLoader().load("map/карта1.tmx"); //!!!!!
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        bg = new int[1];
        bg[0] = map.getLayers().getIndex("фон");
        l1 = new int[2];
        l1[0] = map.getLayers().getIndex("Слой 2");
        l1[1] = map.getLayers().getIndex("Слой 3");

        physX = new PhysX();

        map.getLayers().get("объекты").getObjects().getByType(RectangleMapObject.class); //выбор объектов по типу
        RectangleMapObject tmp = (RectangleMapObject) map.getLayers().get("сеттинг").getObjects().get("hero"); //выбор объекта по имени
        heroRect = tmp.getRectangle();
        body = physX.addObject(tmp);

        Array<RectangleMapObject> objects = map.getLayers().get("объекты").getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        float STEP = 3;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) body.applyForceToCenter (new Vector2(10000, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) body.applyForceToCenter (new Vector2(-10000, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y += STEP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y -= STEP;

        if (Gdx.input.isKeyPressed(Input.Keys.P)) camera.zoom += 0.01f;
        if (Gdx.input.isKeyPressed(Input.Keys.O) && camera.zoom > 0) camera.zoom -= 0.01f;

        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.update();

        ScreenUtils.clear(Color.BLACK);

        mapRenderer.setView(camera);
        mapRenderer.render(bg);

        System.out.println(body.getLinearVelocity());

        batch.setProjectionMatrix(camera.combined);
        heroRect.x = body.getPosition().x - heroRect.width/2;
        heroRect.y = body.getPosition().y - heroRect.height/2;
        batch.begin();
        batch.draw(img, heroRect.x, heroRect.y, heroRect.width, heroRect.height);
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.GOLD);
//        for (int i = 0; i < objects.size; i++) {
//            Rectangle mapSize = objects.get(i).getRectangle();
//            shapeRenderer.rect(mapSize.x, mapSize.y, mapSize.width, mapSize.height);
//        }
//        shapeRenderer.end();

        mapRenderer.render(l1);

        physX.step();
        physX.debugDraw(camera);
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
    }
}
