/*
 * DefaultTranslateTest.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.pluginframework.translate;

import com.publicuhc.pluginframework.locale.LocaleProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, UUID.class})
public class DefaultTranslateTest {

    private DefaultTranslate translate;
    private Player englishPlayer;
    private Player frenchPlayer;

    @Test
    public void testTranslateNoVars() {
        assertThat(translate.translate("testkeynovars", Locale.ENGLISH)).isEqualTo("englishnovars");
        assertThat(translate.translate("testkeynovars", Locale.FRENCH)).isEqualTo("frenchnovars");
        assertThat(translate.translate("novalidkey", Locale.ENGLISH)).isEqualTo("novalidkey");
        assertThat(translate.translate("novalidkey", Locale.FRENCH)).isEqualTo("novalidkey");
    }

    @Test
    public void testTranslateSingleVar() {
        assertThat(translate.translate("testkeyonevar", Locale.ENGLISH, "one")).isEqualTo("test key one var");
    }

    @Test
    public void testTranslateColourCode() {
        assertThat(translate.translate("testkeycolour", Locale.ENGLISH)).isEqualTo(ChatColor.RED + "test key");
    }

    @Test
    public void testTranslateMultiVar() {
        assertThat(translate.translate("testkeymultivar", Locale.ENGLISH, "one", "two")).isEqualTo("test one two one test");
    }

    @Test
    public void testTranslatePlayer()
    {
        assertThat(translate.translate("testkeynovars", englishPlayer)).isEqualTo("englishnovars");
        assertThat(translate.translate("testkeynovars", frenchPlayer)).isEqualTo("frenchnovars");
    }

    @Before
    public void onSetUp() {
        final File dataFolder = new File("target" + File.separator + "testdatafolder");
        if (dataFolder.exists()) {
            deleteDirectory(dataFolder);
        }
        dataFolder.mkdir();

        englishPlayer = mock(Player.class);
        frenchPlayer = mock(Player.class);
        LocaleProvider provider = mock(LocaleProvider.class);
        when(provider.localeForCommandSender(englishPlayer)).thenReturn(Locale.ENGLISH);
        when(provider.localeForCommandSender(frenchPlayer)).thenReturn(Locale.FRENCH);

        translate = new DefaultTranslate(
                provider,
                new YamlControl(dataFolder),
                mock(PluginLogger.class),
                mock(Plugin.class)
        );
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return (path.delete());
    }
}
