package com.xalate.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Screens.PlayScreen;
import com.xalate.mariobros.Sprites.TileObjects.Brick;
import com.xalate.mariobros.Sprites.TileObjects.Coin;
import com.xalate.mariobros.Sprites.Enemies.Goomba;

public class B2WorldCreator {
    private World world;
    private TiledMap map;
    private Array<Goomba> goombas;
    public B2WorldCreator(PlayScreen screen){
        this.world = screen.getWorld();
        this.map = screen.getMap();



        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;
        //Creating ground bodies/fixtures
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM,rectangle.getHeight() / 2 / MarioBros.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        //Creating pipes bodies/fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM,rectangle.getHeight() / 2 / MarioBros.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fixtureDef);
        }

        //Creating coin bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            new Coin(screen,object);
        }


        //Creating bricks bodies/fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            new Brick(screen,object);
        }

        //create all goombas
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            goombas.add(new Goomba(screen,rectangle.getX() / MarioBros.PPM,rectangle.getY() / MarioBros.PPM));
        }

    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
