/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 31.08.19, 11:41
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

package org.mcnative.service.entity.projectile.arrow;

import java.util.List;

public interface TippedArrow extends Arrow {

    //PotionData getBasePotionData();

    boolean hasCustomEffects();

    //boolean hasCustomEffect(PotionEffectType type);

    //List<PotionEffect> getCustomEffects();

    //boolean addCustomEffect(PotionEffect effect, boolean overwrite);

    //boolean removeCustomEffect(PotionEffectType type);

    void clearCustomEffects();

    //Color getColor();

    //void setColor(Color color);
}