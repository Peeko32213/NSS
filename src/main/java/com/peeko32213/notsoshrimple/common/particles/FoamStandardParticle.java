package com.peeko32213.notsoshrimple.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoamStandardParticle extends TextureSheetParticle {
    protected FoamStandardParticle(ClientLevel level, double Xcoord, double Ycoord, double Zcoord,
                                   SpriteSet spriteSet, double xspeed, double yspeed, double zspeed) {
        super(level, Xcoord, Ycoord, Zcoord);

        this.friction = 0.2F;
        //originally 0.8F
        this.xd = xspeed;
        this.yd = yspeed;
        this.zd = zspeed;
        this.quadSize *= 0.85F;
        this.lifetime = 100;
        //^lifetime in ticks
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
        //rgb colours^
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = (-(1/(float)lifetime) * age + 1);
        this.xd = (this.xd/lifetime)*age;
        this.yd = (this.yd/lifetime)*age;
        this.zd = (this.zd/lifetime)*age;
        //this.setParticleSpeed(this.get);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public final SpriteSet sprites;

        public Provider(SpriteSet spriteset) {
            this.sprites = spriteset;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new FoamStandardParticle(level, x, y, z, this.sprites, dx, dy, dz);
            //TODO: replace 400 with a customizable duration value
        }
    }
}
