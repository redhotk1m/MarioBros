package com.xalate.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Screens.PlayScreen;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected Fixture fixture;
    protected MapObject object;

    public InteractiveTileObject(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();


        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bodyDef);

        polygonShape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM,bounds.getHeight() / 2 / MarioBros.PPM);
        fixtureDef.shape = polygonShape;
        fixture = body.createFixture(fixtureDef);

        //body.createFixture(fixtureDef);
    }

    public abstract void onHeadHit();
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * MarioBros.PPM / 16),(int)((body).getPosition().y * MarioBros.PPM / 16));
    }
}
