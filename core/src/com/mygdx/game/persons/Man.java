package com.mygdx.game.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class Man {
    HashMap<Actions, Animation<TextureRegion>> manAssetss;
    private final float FPS = 1/7f;
    private float time;
    private boolean canJump;
    private Animation<TextureRegion> baseAnm;
    private boolean loop;
    private TextureAtlas atl;

    private Dir dir;
    private static float dScale = 2.8f;

    public enum Dir{LEFT, RIGHT}

    public Man(){
        manAssetss = new HashMap<>();
        atl = new TextureAtlas("atlas/man.atlas");
        manAssetss.put(Actions.JUMP, new Animation<TextureRegion>(FPS, atl.findRegions("jump")));
        manAssetss.put(Actions.RUN, new Animation<TextureRegion>(FPS, atl.findRegions("run")));
        manAssetss.put(Actions.STAND, new Animation<TextureRegion>(FPS, atl.findRegions("stand")));
        manAssetss.put(Actions.SHOOT, new Animation<TextureRegion>(FPS, atl.findRegions("shoot")));
        baseAnm = manAssetss.get(Actions.STAND);
        loop = true;
        dir = Dir.LEFT;
    }

    public boolean isCanJump() {return canJump;}
    public void setCanJump(boolean canJump) {this.canJump = canJump;}
    public void setDir(Dir dir){this.dir = dir;}
    public void setLoop(boolean loop) {this.loop = loop;}
    public void setFPS(Vector2 vector, boolean onGround) {
        if (vector.x > 0.1f) setDir(Dir.RIGHT);
        if (vector.x < -0.1f) setDir(Dir.LEFT);
        float tmp = (float) (Math.sqrt(vector.x*vector.x + vector.y*vector.y))*10;
        setState(Actions.STAND);
        if (Math.abs(vector.x) > 0.25f && Math.abs(vector.y) < 10 && onGround) {
            setState(Actions.RUN);
            baseAnm.setFrameDuration(1/tmp);
        }
        if (Math.abs(vector.y) > 1 && !onGround) {
            setState(Actions.JUMP);
            baseAnm.setFrameDuration(FPS);
        }
    }

    public float setTime(float deltaTime) {
        time += deltaTime;
        return time;
    }

    public void setState(Actions state){
        baseAnm = manAssetss.get(state);
        switch (state){
            case STAND: loop = true; baseAnm.setFrameDuration(FPS);break;
            case JUMP: loop = false; break;
            default: loop = true;
        }
    }

    public TextureRegion getFrame() {
        if (time > baseAnm.getAnimationDuration() && loop) time = 0;
        if (time > baseAnm.getAnimationDuration()) time = 0;
        TextureRegion tr = baseAnm.getKeyFrame(time);
        if (!tr.isFlipX() && dir == Dir.LEFT) tr.flip(true, false);
        if (tr.isFlipX() && dir == Dir.RIGHT) tr.flip(true, false);
        return tr;
    }

    public Rectangle getRect(OrthographicCamera camera, TextureRegion region) {
        float cx = Gdx.graphics.getWidth()/2 - region.getRegionWidth()/2 / camera.zoom/ dScale;
        float cy = Gdx.graphics.getHeight()/2 - region.getRegionHeight()/2 / camera.zoom/ dScale;
        float cW = region.getRegionWidth() / camera.zoom / dScale;
        float cH = region.getRegionHeight() / camera.zoom / dScale;
        return new Rectangle(cx , cy, cW, cH);
    }

    public void dispose(){
        atl.dispose();
        this.manAssetss.clear();
    }
}
