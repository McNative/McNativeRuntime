/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.08.19 10:45
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

package org.mcnative.service.world.region;

import org.mcnative.service.entity.player.Player;
import org.mcnative.service.world.Location;
import org.mcnative.service.world.Point;
import org.mcnative.service.world.World;
import org.mcnative.service.world.WorldSequence;
import org.mcnative.service.world.block.Block;

public interface Region extends WorldSequence {

    String getName();

    World getWorld();

    Point getPointA();

    Point getPointB();

    Point[] getCorners();

    Block[] getCornerBlocks();

    default boolean isInside(Player player){
        return isInside(player.getLocation());
    }

    boolean isInside(Location player);

    default boolean isOutside(Player player){
        return isOutside(player.getLocation());
    }

    boolean isOutside(Location player);

    void setName(String name);

    void setPointA(Point location);

    void setPointB(Point location);


}
