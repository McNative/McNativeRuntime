/*
 * (C) Copyright 2019 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 12.08.19, 19:35
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

package org.mcnative.service.inventory;

import org.mcnative.common.protocol.MinecraftProtocolVersion;
import org.mcnative.common.protocol.support.BEProtocolSupport;
import org.mcnative.common.protocol.support.JEProtocolSupport;
import org.mcnative.service.inventory.item.ItemStack;

@JEProtocolSupport(min= MinecraftProtocolVersion.JE_1_14)
@BEProtocolSupport(supported = false)
public interface GrindstoneInventory extends Inventory{

    ItemStack getInputAbove();

    void setInputAbove(ItemStack item);

    ItemStack getInputBelow();

    void setInputBelow(ItemStack item);

    ItemStack getOutput();

    void setOutput(ItemStack item);

    void clearItemsOnClose(boolean clear);

}
