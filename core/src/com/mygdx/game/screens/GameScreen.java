package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Anim;
import com.mygdx.game.Main;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture img;
    private final Anim animation;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Rectangle mapSize;
    private final ShapeRenderer shapeRenderer;

    public GameScreen(Main game) {
        animation = new Anim("ezgif-3-7ce6b9eafc.png", 5,4, Animation.PlayMode.LOOP);
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        img = new Texture("titleSpecter.png");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.25f;

        TiledMap map = new TmxMapLoader().load("map/карта1.tmx"); //!!!!!
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        map.getLayers().get("объекты").getObjects().getByType(RectangleMapObject.class); //выбор объектов по типу
        RectangleMapObject tmp = (RectangleMapObject) map.getLayers().get("объекты").getObjects().get("камера"); //выбор объекта по имени
        camera.position.x = tmp.getRectangle().x;
        camera.position.y = tmp.getRectangle().y;

        tmp = (RectangleMapObject) (map.getLayers().get("объекты").getObjects().get("граница"));
        mapSize = tmp.getRectangle();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        float STEP = 3;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && mapSize.x < (camera.position.x-1) ) camera.position.x -= STEP;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && (mapSize.x + mapSize.width) > (camera.position.x+1) ) camera.position.x += STEP;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y += STEP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y -= STEP;



        if (Gdx.input.isKeyPressed(Input.Keys.P)) camera.zoom += 0.01f;
        if (Gdx.input.isKeyPressed(Input.Keys.O) && camera.zoom > 0) camera.zoom -= 0.01f;

        camera.update();

        ScreenUtils.clear(Color.BLUE);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(mapSize.x, mapSize.y, mapSize.width, mapSize.height);
        shapeRenderer.end();

        batch.begin();
        animation.setTime(Gdx.graphics.getDeltaTime());
        TextureRegion trTmp = animation.getFrame();
        batch.draw(trTmp, Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f);
        batch.end();
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
    }
}
