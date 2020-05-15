package com.xalate.mariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.xalate.mariobros.MarioBros;
import com.xalate.mariobros.Sprites.Enemies.Enemy;
import com.xalate.mariobros.Sprites.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        if ("head".equals(fixA.getUserData()) || "head".equals(fixB.getUserData())){
            Fixture head = fixA.getUserData().equals("head") ? fixA : fixB;
            Fixture object = head.equals(fixA) ? fixB : fixA;

            if (object.getUserData() != null && object.getUserData() instanceof InteractiveTileObject)
                ((InteractiveTileObject)object.getUserData()).onHeadHit();
        }
        switch (cDef){
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                Gdx.app.log("Mario died","dead");
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("End contact","");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
