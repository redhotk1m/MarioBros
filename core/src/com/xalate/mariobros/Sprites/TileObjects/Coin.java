package com.xalate.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Scenes.Hud;
import com.xalate.mariobros.Screens.PlayScreen;
import com.xalate.mariobros.Sprites.Items.ItemDef;
import com.xalate.mariobros.Sprites.Items.Mushroom;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; //Tile begynner på 1, så ID er egentlig 27
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK_COIN)
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if (object.getProperties().containsKey("mushroom")) {
                Hud.addScore(100);
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();

            }
            else
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));

    }

}
