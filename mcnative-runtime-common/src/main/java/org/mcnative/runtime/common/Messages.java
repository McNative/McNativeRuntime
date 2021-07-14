/*
 * (C) Copyright 2020 The McNative Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.04.20, 20:37
 * @web %web%
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

package org.mcnative.runtime.common;

import net.pretronic.libraries.document.annotations.DocumentIgnored;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageKeyComponent;

public final class Messages {

    @DocumentIgnored
    public static final String CONSOLE_PREFIX = "[McNative]";

    public static final MessageKeyComponent PREFIX = Text.ofMessageKey("mcnative.prefix");

    public static MessageKeyComponent NO_PERMISSIONS = Text.ofMessageKey("mcnative.nopermissions");

    public static MessageKeyComponent COMMAND_MCNATIVE_PASTE_STARTING = Text.ofMessageKey("mcnative.command.manage.paste.staring");
    public static MessageKeyComponent COMMAND_MCNATIVE_PASTE_SUCCESSFUL = Text.ofMessageKey("mcnative.command.manage.paste.successful");

    public static MessageKeyComponent COMMAND_MCNATIVE_HELP = Text.ofMessageKey("mcnative.command.manage.help");

    public static MessageKeyComponent COMMAND_MCNATIVE_INFO = Text.ofMessageKey("mcnative.command.manage.info");

    public static MessageKeyComponent COMMAND_MCNATIVE_VERSION = Text.ofMessageKey("mcnative.command.manage.version");

    public static MessageKeyComponent COMMAND_MCNATIVE_INVALID_USAGE = Text.ofMessageKey("mcnative.command.manage.invalid.usage");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_LIST = Text.ofMessageKey("mcnative.command.manage.plugin.list");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_NOTFOUND = Text.ofMessageKey("mcnative.command.manage.plugin.notfound");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_ALREADY_ENABLED = Text.ofMessageKey("mcnative.command.manage.plugin.already.enabled");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_ALREADY_DISABLED = Text.ofMessageKey("mcnative.command.manage.plugin.already.disabled");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_ENABLE_STARTING = Text.ofMessageKey("mcnative.command.manage.plugin.enable.starting");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_ENABLE_SUCCESSFULLY = Text.ofMessageKey("mcnative.command.manage.plugin.enable.successfully");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_ENABLE_FAILED = Text.ofMessageKey("mcnative.command.manage.plugin.enable.failed");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_DISABLE_STARTING = Text.ofMessageKey("mcnative.command.manage.plugin.disable.starting");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_DISABLE_SUCCESSFULLY = Text.ofMessageKey("mcnative.command.manage.plugin.disable.successfully");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_DISABLE_FAILED = Text.ofMessageKey("mcnative.command.manage.plugin.disable.failed");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_LOAD_STARTING = Text.ofMessageKey("mcnative.command.manage.plugin.load.starting");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_LOAD_SUCCESSFULLY = Text.ofMessageKey("mcnative.command.manage.plugin.load.successfully");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_LOAD_FAILED = Text.ofMessageKey("mcnative.command.manage.plugin.load.failed");

    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_UNLOAD_STARTING = Text.ofMessageKey("mcnative.command.manage.plugin.unload.starting");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_UNLOAD_SUCCESSFULLY = Text.ofMessageKey("mcnative.command.manage.plugin.unload.successfully");
    public static MessageKeyComponent COMMAND_MCNATIVE_PLUGIN_UNLOAD_FAILED = Text.ofMessageKey("mcnative.command.manage.plugin.unload.failed");

    public static MessageKeyComponent COMMAND_MCNATIVE_LANUGAUE_SELECT = Text.ofMessageKey("mcnative.command.language.select");
    public static MessageKeyComponent COMMAND_MCNATIVE_LANUGAUE_LIST = Text.ofMessageKey("mcnative.command.language.list");

    public static MessageKeyComponent COMMAND_MCNATIVE_GUI_NOT_FOUND = Text.ofMessageKey("mcnative.command.gui.notFound");
    public static MessageKeyComponent COMMAND_MCNATIVE_GUI_LIST = Text.ofMessageKey("mcnative.command.gui.list");

    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_TEXT = Text.ofMessageKey("mcnative.textinput.validator.error.text");
    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_BOOLEAN = Text.ofMessageKey("mcnative.textinput.validator.error.boolean");
    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_NUMBER = Text.ofMessageKey("mcnative.textinput.validator.error.number");
    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_DECIMAL = Text.ofMessageKey("mcnative.textinput.validator.error.decimal");
    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_COLOR = Text.ofMessageKey("mcnative.textinput.validator.error.color");
    public static final MessageKeyComponent TEXT_INPUT_VALIDATOR_ERROR_OBJECT = Text.ofMessageKey("mcnative.textinput.validator.error.object");
}
