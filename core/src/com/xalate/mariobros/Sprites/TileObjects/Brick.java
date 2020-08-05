package com.xalate.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Scenes.Hud;
import com.xalate.mariobros.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick","Collision");
        setCategoryFilter(MarioBros.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);//Bør ikke være static, fix later
        MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
