/*
 * DefaultTranslateTest.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.configuration.DefaultConfigurator;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, UUID.class})
public class DefaultTranslateTest {

    private DefaultTranslate translate;
    private Configurator configurator;

    @Test
    public void testSetLocaleForPlayer() {
        Player p = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(uuid);
        translate.setLocaleForPlayer(p, "ru");

        assertThat(configurator.getConfig("locales").getString("players." + uuid), is(equalTo("ru")));

        mockStatic(Bukkit.class);
        PluginManager manager = mock(PluginManager.class);
        when(Bukkit.getPluginManager()).thenReturn(manager);

        configurator.reloadConfig("locales");

        assertThat(configurator.getConfig("locales").getString("players." + uuid), is(equalTo("ru")));
    }

    @Test
    public void testGetLocaleForSender() {
        Player p = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(uuid);

        translate.setLocaleForPlayer(p, "fr");
        assertThat(translate.getLocaleForSender(p), is(equalTo("fr")));

        FileConfiguration config = configurator.getConfig("locales");

        assertThat(translate.getLocaleForSender(mock(ConsoleCommandSender.class)), is(equalTo(config.getString("console"))));
        assertThat(translate.getLocaleForSender(mock(BlockCommandSender.class)), is(equalTo(config.getString("commandBlock"))));
        assertThat(translate.getLocaleForSender(mock(RemoteConsoleCommandSender.class)), is(equalTo(config.getString("remoteConsole"))));

        Player p2 = mock(Player.class);
        UUID uuid2 = UUID.randomUUID();
        when(p2.getUniqueId()).thenReturn(uuid2);

        assertThat(translate.getLocaleForSender(p2), is(equalTo(config.getString("default"))));
    }

    @Test
    public void testTranslateNoVars() {
        assertThat(translate.translate("novalidkey", "test"), is(equalTo("TRANSLATION KEY NOT SET FOR novalidkey")));
        assertThat(translate.translate("testkeynovars", "test"), is(equalTo("testkeynovars")));
    }

    @Test
    public void testTranslateSingleVar() {
        assertThat(translate.translate("testkeyonevar", "test", "amount", "one"), is(equalTo("test key one var")));
    }

    @Test
    public void testTranslateMultiVar() {
        Map<String, String> trans = new HashMap<String, String>();
        trans.put("var1", "one");
        trans.put("var2", "two");
        assertThat(translate.translate("testkeymultivar", "test", trans), is(equalTo("test one two one test")));
    }

    @Before
    public void onSetUp() {
        final File dataFolder = new File("target"+File.separator+"testdatafolder");
        if(dataFolder.exists()){
            deleteDirectory(dataFolder);
        }
        dataFolder.mkdir();

        Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Configurator.class).to(DefaultConfigurator.class);
                bind(File.class).annotatedWith(Names.named("dataFolder")).toInstance(dataFolder);
                bind(ClassLoader.class).toInstance(getClass().getClassLoader());
            }
        });
        configurator = i.getInstance(Configurator.class);
        translate = new DefaultTranslate(configurator);
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
