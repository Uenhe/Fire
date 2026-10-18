package fire.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import fire.content.FRItems;
import fire.world.meta.FRAttribute;
import mindustry.gen.Building;
import mindustry.gen.Iconc;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class FleshGenerator extends mindustry.world.blocks.power.ConsumeGenerator{
    public TextureRegion region2;

    public FleshGenerator(String name){
        super(name);
        noUpdateDisabled = true;
        buildType = FleshGeneratorBuild::new;
        hasLiquids = false;
    }


    public float getEfficiency(Tile tile){
        if(tile == null) return 0.0f;

        float steamBlockCounting = 0.0f, efficiencyCounting = 0.0f;
        var tiles = tile.getLinkedTilesAs(this, tempTiles);
        for(var other : tiles){
            efficiencyCounting += other.floor().attributes.get(FRAttribute.flesh) * 1.25f;
            efficiencyCounting += other.floor().attributes.get(FRAttribute.grass) / 2.25f * 0.4f;
            efficiencyCounting += other.floor().attributes.get(Attribute.spores) / 2.7f * 0.8f;
            efficiencyCounting += other.floor().attributes.get(Attribute.heat) / 6.75f * 1.25f;
            efficiencyCounting += other.floor().attributes.get(Attribute.oil) / 13.5f * 0.75f;
            steamBlockCounting += other.floor().attributes.get(Attribute.steam);
        }

        if(steamBlockCounting >= 9.0f)return 1.0f;
        return Math.min(efficiencyCounting, 1);
    }

    public int getCons(float efficiency){
        return efficiency > 0.8f ? 1:
            efficiency > 0.6f ? 2:
                efficiency > 0.4f ? 3:
                    efficiency > 0.2f ? 4:
                        5;
    }

    private float estimateEfficiency(Floor floor){
        return (floor.attributes.get(FRAttribute.flesh) * 1.25f +
            floor.attributes.get(FRAttribute.flesh) / 2.25f * 0.4f +
            floor.attributes.get(Attribute.spores) / 2.7f * 0.8f +
            floor.attributes.get(Attribute.heat) / 6.75f * 1.25f +
            floor.attributes.get(Attribute.oil) / 13.5f * 0.75f +
            floor.attributes.get(Attribute.steam) / 9.0f) * size * size;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.tiles, mt->{
            Table table = mt.row().table(Styles.grayPanel, t -> {}).left().get();
            int i = 0;
            for(var block : content.blocks()){
                if(!(block instanceof Floor))continue;
                float efficiency = estimateEfficiency((Floor)block);
                if(efficiency <= 0)continue;
                table.stack(
                    new Table(t -> t.image(block.uiIcon).size(size * 16).pad(16.0f).scaling(Scaling.fit)),
                    new Table(t -> t.top().right().add("[accent]+" + Strings.fixed(efficiency * 100, 0) + "%").style(Styles.outlineLabel).fontScale(1))
                );
                if(++i % 10 == 0) table.row();
            }
        });

        stats.remove(Stat.booster);
        stats.add(Stat.booster, table -> {
            table.table(Styles.grayPanel, t -> {
                t.left().image(FRItems.flesh.uiIcon).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                t.left().table(info -> {
                    info.left().add(FRItems.flesh.localizedName).left().row();
                    info.right().add("[accent]1.5" + StatUnit.timesSpeed.localized()).right().row();
                });
            }).growX().pad(5.0f).row();
        });
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        float efficiency = getEfficiency(world.tile(x, y));
        drawPlaceText(Iconc.box + " : " + getCons(efficiency) + "x", x, y, false);
        drawPlaceText(Iconc.power + " : " + Strings.fixed(efficiency + 1, 2) + "x", x, y + 1, valid);

        super.drawPlace(x, y, rotation, valid);

    }

    public class FleshGeneratorBuild extends GeneratorBuild{

        private int cons;
        private float sum;
        private float totalTime;
        private float boostTime;
        private float warmup;
        private float efficiencyMultiplier;

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            sum = getEfficiency(tile) + 1;
            cons = getCons(sum - 1);
        }

        @Override
        public void updateEfficiencyMultiplier(){
            if(filterItem != null){
                float m = filterItem.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return (this.block.consumesItem(item) || item == FRItems.flesh) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public void updateTile(){

            boolean valid = efficiency > 0;
            //take in items periodically
            if(hasItems && valid  && generateTime <= 0f){
                consume();
                consumeEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
                generateTime = 1f;
            }

            if(this.items.get(FRItems.flesh) > 0 && boostTime <= 0){
                boostTime = 1;
                this.items.remove(FRItems.flesh,1);
            }

            efficiency = (sum + (boostTime > 0 ? 0.5f : 0)) * (generateTime > 0 ? 1 : 0);
            productionEfficiency = efficiency * efficiencyMultiplier;


            warmup = Mathf.lerpDelta(warmup, valid ? 1f : 0f, warmupSpeed);

            totalTime += warmup * Time.delta;

            //randomly produce the effect
            if(Mathf.chanceDelta(effectChance)){
                generateEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
            }

            //generation time always goes down, but only at the end so consumeTriggerValid doesn't assume fake items
            generateTime -= delta() / (itemDuration) * cons;
            if(boostTime > 0 && generateTime > 0)boostTime -= delta() / 300.0f;
        }

        public Color getCol(){
            return this.tileOn().floor().mapColor;
        }

        @Override
        public void draw(){
            Draw.z(Layer.blockAdditive);
            Draw.color(getCol());
            Draw.mixcol(Color.white,0.4f + Mathf.absin(Time.time, 10, 0.3f) * this.warmup());
            Draw.alpha(Math.max(0, sum - 1) + Mathf.absin(Time.time, 10, 0.6f) * this.warmup());
            Draw.rect(region2, this.x, this.y);
            Draw.reset();
            super.draw();
        }

        @Override
        public boolean consumeTriggerValid(){
            return generateTime > 0;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalTime;
        }
    }

    @Override
    public void load(){
        region2 = Core.atlas.find(this.name + "-floor");
        super.load();
    }
}
