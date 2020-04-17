
package com.client.shoot;

import com.model.Bullet;

public class UserState {
    private int[] heroPos;
    private Bullet[] bullets;

    public UserState(int[] heroPos, Bullet[] bullets) {
        this.heroPos = heroPos;
        this.bullets = bullets;
    }

    public int[] getHeroPos() {
        return heroPos;
    }

    public void setHeroPos(int[] heroPos) {
        this.heroPos = heroPos;
    }

    public Bullet[] getBullets() {
        return bullets;
    }

    public void setBullets(Bullet[] bullets) {
        this.bullets = bullets;
    }
}
