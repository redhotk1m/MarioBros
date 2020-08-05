package com.xalate.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.tools.javac.jvm.Items;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Scenes.Hud;
import com.xalate.mariobros.Sprites.Enemies.Enemy;
import com.xalate.mariobros.Sprites.Items.Item;
import com.xalate.mariobros.Sprites.Items.ItemDef;
import com.xalate.mariobros.Sprites.Items.Mushroom;
import com.xalate.mariobros.Sprites.Mario;
import com.xalate.mariobros.Tools.B2WorldCreator;
import com.xalate.mariobros.Tools.WorldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    //reference to our Game, used to set screens
    private MarioBros game;

    private TextureAtlas atlas;

    //basic playscreen variables
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private B2WorldCreator creator;

    //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        //Libgdx AssetManager <-- Sjekk ut dette

        this.game = game;
        //Create cam used to follow mario through cam world
        gameCam = new OrthographicCamera();
        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,MarioBros.V_HEIGHT / MarioBros.PPM,gameCam);
        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("customMarioMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        //Initially set our gameCam to be centered correctly at the start of our map
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0,-10),true);
        box2DDebugRenderer = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg",Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class){
                items.add(new Mushroom(this,idef.position.x,idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        //if our user is holding down mouse, move our camera through the game world
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            player.b2body.applyLinearImpulse(new Vector2(0,4),player.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0),player.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2body.getWorldCenter(),true);
        }
    }

    public void update(float dt){
        //Handle user input first
        handleInput(dt);
        handleSpawningItems();

        //Read more about this
        //takes 1 step in the physics simulation (60 times per second)
        world.step(1/60f,6,2);
        player.update(dt);
        for (Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + gameCam.viewportWidth + 224 / MarioBros.PPM) //Tiles from center = 12 + 2 = 14 * 16 pixels = 224
                enemy.b2body.setActive(true);
        }
        for (Item item : items){
            item.update(dt);
        }
        hud.update(dt);

        //attach our gamecam to our players.x coordinate
        gameCam.position.x = player.b2body.getPosition().x;


        //update our gamecam with correct coordinates
        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        //seperate our update logic from render
        update(delta);
        //clear the game screen with black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render our game map
        renderer.render();
        //renderer our Box2DDebugLines
        box2DDebugRenderer.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        for (Item item : items){
            item.draw(game.batch);
        }
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }


    public World getWorld(){
        return world;
    }

    public TiledMap getMap(){
        return map;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }
}
