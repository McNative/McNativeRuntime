/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.09.19, 18:15
 *
 * The McNative Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mcnative.runtime.protocol.java.codec.sound.effect;

import io.netty.buffer.ByteBuf;
import org.mcnative.runtime.api.connection.MinecraftConnection;
import org.mcnative.runtime.api.player.sound.MinecraftSound;
import org.mcnative.runtime.api.protocol.packet.MinecraftPacketCodec;
import org.mcnative.runtime.api.protocol.packet.PacketDirection;
import org.mcnative.runtime.api.protocol.packet.type.sound.MinecraftSoundEffectPacket;
import org.mcnative.runtime.protocol.java.MinecraftProtocolUtil;

import java.util.HashMap;
import java.util.Map;

public class MinecraftSoundEffectCodecV1_7 implements MinecraftPacketCodec<MinecraftSoundEffectPacket> {

    private final Map<String,String> LEGACY_SOUND_MAP = new HashMap<>();

    public MinecraftSoundEffectCodecV1_7(){
        registerMappedSounds();
    }

    @Override
    public void read(MinecraftSoundEffectPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        packet.setSoundName(MinecraftProtocolUtil.readString(buffer));
        packet.setPositionX(buffer.readInt());
        packet.setPositionY(buffer.readInt());
        packet.setPositionZ(buffer.readInt());
        packet.setVolume(buffer.readFloat());
        packet.setPitch(buffer.readByte());
    }

    @Override
    public void write(MinecraftSoundEffectPacket packet, MinecraftConnection connection, PacketDirection direction, ByteBuf buffer) {
        String output = LEGACY_SOUND_MAP.get(packet.getSoundName());
        MinecraftProtocolUtil.writeString(buffer,output);
        buffer.writeInt(packet.getPositionX());
        buffer.writeInt(packet.getPositionY());
        buffer.writeInt(packet.getPositionZ());
        buffer.writeFloat(packet.getVolume());
        buffer.writeByte((int)(packet.getPitch()* 63.5));
    }

    private void registerMappedSounds(){
        LEGACY_SOUND_MAP.put(MinecraftSound.AMBIENT_CAVE,"ambient.cave.cave");
        LEGACY_SOUND_MAP.put(MinecraftSound.WEATHER_RAIN,"ambient.weather.rain");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_LIGHTNING_BOLT_THUNDER,"ambient.weather.thunder");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_BIG_FALL,"damage.fallbig");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_SMALL_FALL,"damage.fallsmall");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_FIRE_AMBIENT,"fire.fire");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_BLASTFURNACE_FIRE_CRACKLE,"fire.ignite");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_LAVA_AMBIENT,"liquid.lava");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_LAVA_POP,"liquid.lavapop");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_LAVA_EXTINGUISH,"liquid.splash");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WATER_AMBIENT,"liquid.water");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BLAZE_AMBIENT,"mob.blaze.breathe");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BLAZE_DEATH,"mob.blaze.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BLAZE_HURT,"mob.blaze.hit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CAT_HISS,"mob.cat.hiss");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CAT_HURT,"mob.cat.hitt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CAT_AMBIENT,"mob.cat.meow");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CAT_PURR,"mob.cat.purr");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CAT_PURREOW,"mob.cat.purreow");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CHICKEN_HURT,"mob.chicken.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CHICKEN_EGG,"mob.chicken.plop");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_COW_HURT,"mob.cow.hurt");
       // LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CREEPER_PRIMED,"mob.creeper");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CREEPER_DEATH,"mob.creeperdeath");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_DEATH,"mob.endermen.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_HURT,"mob.endermen.hit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_AMBIENT,"mob.endermen.idle");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_TELEPORT,"mob.endermen.portal");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_SCREAM,"mob.endermen.scream");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDERMAN_STARE,"mob.endermen.stare");
        //LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_,"mob.ghast.affectionate scream");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_AMBIENT,"mob.ghast.charge");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_DEATH,"mob.ghast.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_SHOOT,"mob.ghast.fireball");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_WARN,"mob.ghast.moan");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GHAST_SCREAM,"mob.ghast.scream");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_IRON_GOLEM_DEATH,"mob.irongolem.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_IRON_GOLEM_HURT,"mob.irongolem.hit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_IRON_GOLEM_ATTACK,"mob.irongolem.throw");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_IRON_GOLEM_STEP,"mob.irongolem.walk");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_MAGMA_CUBE_DEATH,"mob.magmacube.big");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_MAGMA_CUBE_JUMP,"mob.magmacube.jump");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_MAGMA_CUBE_DEATH_SMALL,"mob.magmacube.small");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SILVERFISH_HURT,"mob.silverfish.hit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SILVERFISH_DEATH,"mob.silverfish.kill");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SILVERFISH_AMBIENT,"mob.silverfish.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SILVERFISH_STEP,"mob.silverfish.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SKELETON_DEATH,"mob.skeleton.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SKELETON_HURT,"mob.skeleton.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SLIME_ATTACK,"mob.slime.attack");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SPIDER_DEATH,"mob.spider.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_AMBIENT,"mob.wolf.bark");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_DEATH,"mob.wolf.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_GROWL,"mob.wolf.growl");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_HOWL,"mob.wolf.howl");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_HURT,"mob.wolf.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_PANT,"mob.wolf.panting");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_SHAKE,"mob.wolf.shake");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_WHINE,"mob.wolf.whine");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,"mob.zombie.metal");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,"mob.zombie.wood");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,"mob.zombie.woodbreak");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIGLIN_AMBIENT,"mob.zombiepig.zpig");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIGLIN_ANGRY,"mob.zombiepig.zpigangry");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIGLIN_DEATH,"mob.zombiepig.zpigdeath");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIGLIN_HURT,"mob.zombiepig.zpighurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_BASS,"note.bass");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_GUITAR,"note.bassattack");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_BASEDRUM,"note.bd");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_HARP,"note.harp");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_HAT,"note.hat");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_PLING,"note.pling");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_NOTE_BLOCK_SNARE,"note.snare");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_PORTAL_AMBIENT,"portal.portal");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_PORTAL_TRAVEL,"portal.travel");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_PORTAL_TRIGGER,"portal.trigger");
        LEGACY_SOUND_MAP.put(MinecraftSound.ITEM_CROSSBOW_QUICK_CHARGE_1,"random.bow");
        LEGACY_SOUND_MAP.put(MinecraftSound.ITEM_CROSSBOW_QUICK_CHARGE_2,"random.bow");
        LEGACY_SOUND_MAP.put(MinecraftSound.ITEM_CROSSBOW_QUICK_CHARGE_3,"random.bow");
        LEGACY_SOUND_MAP.put(MinecraftSound.ITEM_CROSSBOW_HIT,"random.bowhit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ITEM_BREAK,"random.break");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PLAYER_BREATH,"random.breath");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PLAYER_BURP,"random.burp");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_CHEST_CLOSE,"random.chestclosed");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_CHEST_OPEN,"random.chestopen");
        LEGACY_SOUND_MAP.put(MinecraftSound.UI_BUTTON_CLICK,"random.click");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WOODEN_DOOR_CLOSE,"random.door_close");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WOODEN_DOOR_OPEN,"random.door_open");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_DRINK,"random.drink");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_EAT,"random.eat");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_EXPLODE,"random.explode");
       // LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_,"random.fizz");
       // LEGACY_SOUND_MAP.put(MinecraftSound.N,"random.fuse");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_GLASS_BREAK,"random.glass");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PLAYER_LEVELUP,"random.levelup");
       // LEGACY_SOUND_MAP.put(MinecraftSound.EXPLO,"random.old_explode");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_EXPERIENCE_ORB_PICKUP,"random.orb");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,"random.pop");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_SPLASH,"random.splash");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WOODEN_BUTTON_CLICK_ON,"random.wood click");
        //LEGACY_SOUND_MAP.put(MinecraftSound.CLOTH,"step.cloth");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_GRASS_STEP,"step.grass");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_GRAVEL_STEP,"step.gravel");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_SAND_STEP,"step.sand");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_SNOW_STEP,"step.snow");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_STONE_STEP,"step.stone");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WOOD_STEP,"step.wood");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_PISTON_CONTRACT,"tile.piston.in");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_PISTON_EXTEND,"tile.piston.out");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ARROW_HIT,"damage.hit");
        //LEGACY_SOUND_MAP.put(MinecraftSound.BREAK,"dig.cloth");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_GRASS_BREAK,"dig.grass");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_GRAVEL_BREAK,"dig.gravel");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_SAND_BREAK,"dig.sand");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_SNOW_BREAK,"dig.snow");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_STONE_BREAK,"dig.stone");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_WOOD_BREAK,"dig.wood");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_SWIM,"liquid.swim");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_MINECART_RIDING,"minecart.base");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_MINECART_INSIDE,"minecart.inside");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CHICKEN_AMBIENT,"mob.chicken.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CHICKEN_STEP,"mob.chicken.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_COW_AMBIENT,"mob.cow.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_COW_STEP,"mob.cow.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_CREEPER_PRIMED,"mob.creeper.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIG_DEATH,"mob.pig.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIG_AMBIENT,"mob.pig.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_PIG_STEP,"mob.pig.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SHEEP_AMBIENT,"mob.sheep.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SHEEP_SHEAR,"mob.sheep.shear");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SHEEP_STEP,"mob.sheep.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SKELETON_AMBIENT,"mob.skeleton.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SKELETON_STEP,"mob.skeleton.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SLIME_JUMP,"mob.slime.big");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SLIME_JUMP_SMALL,"mob.slime.small");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SPIDER_AMBIENT,"mob.spider.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_SPIDER_STEP,"mob.spider.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WOLF_STEP,"mob.wolf.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_DEATH,"mob.zombie.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_HURT,"mob.zombie.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_AMBIENT,"mob.zombie.say");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_STEP,"mob.zombie.step");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_GENERIC_HURT,"random.classic_hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_LADDER_STEP,"step.ladder");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BAT_DEATH,"mob.bat.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BAT_HURT,"mob.bat.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BAT_AMBIENT,"mob.bat.idle");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_BAT_LOOP,"mob.bat.takeoff");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDER_DRAGON_DEATH,"mob.enderdragon.end");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDER_DRAGON_GROWL,"mob.enderdragon.growl");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDER_DRAGON_HURT,"mob.enderdragon.hit");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ENDER_DRAGON_AMBIENT,"mob.enderdragon.wings");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WITHER_DEATH,"mob.wither.death");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WITHER_HURT,"mob.wither.hurt");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WITHER_AMBIENT,"mob.wither.idle");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WITHER_SHOOT,"mob.wither.shoot");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_WITHER_SPAWN,"mob.wither.spawn");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_INFECT,"mob.zombie.infect");
        //LEGACY_SOUND_MAP.put(MinecraftSound.ZOMBIE_,"mob.zombie.remedy");
        LEGACY_SOUND_MAP.put(MinecraftSound.ENTITY_ZOMBIE_VILLAGER_CONVERTED,"mob.zombie.unfect");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_ANVIL_DESTROY,"random.anvil_break");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_ANVIL_LAND,"random.anvil_land");
        LEGACY_SOUND_MAP.put(MinecraftSound.BLOCK_ANVIL_USE,"random.anvil_use");
    }
}
