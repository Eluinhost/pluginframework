package com.publicuhc;

import com.publicuhc.commands.routing.DefaultRouter;
import com.publicuhc.configuration.DefaultConfigurator;
import com.publicuhc.translate.DefaultTranslate;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FrameworkJavaPlugin.class, PluginDescriptionFile.class, JavaPluginLoader.class })
public class FrameworkJavaPluginTest {

    @Test
    public void testUseDefaultModules() throws Exception {
        //TODO work out how to test a JavaPlugin - cos Bukkit
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("bin/test/plugins/testplugin");

        PluginLogger logger = mock(PluginLogger.class);
        whenNew(PluginLogger.class).withAnyArguments().thenReturn(logger);

        FrameworkJavaPlugin plugin = new FrameworkJavaPlugin(loader, server, pdf, file1, file1) { };

        assertThat(plugin.getConfigurator(), is(nullValue()));
        assertThat(plugin.getRouter(), is(nullValue()));
        assertThat(plugin.getTranslate(), is(nullValue()));

        plugin.onLoad();

        assertThat(plugin.getConfigurator(), is(instanceOf(DefaultConfigurator.class)));
        assertThat(plugin.getTranslate(), is(instanceOf(DefaultTranslate.class)));
        assertThat(plugin.getRouter(), is(instanceOf(DefaultRouter.class)));

        plugin.onEnable();

        assertThat(plugin.getConfigurator(), is(instanceOf(DefaultConfigurator.class)));
        assertThat(plugin.getTranslate(), is(instanceOf(DefaultTranslate.class)));
        assertThat(plugin.getRouter(), is(instanceOf(DefaultRouter.class)));
    }
}
