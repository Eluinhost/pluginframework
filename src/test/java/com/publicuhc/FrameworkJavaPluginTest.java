package com.publicuhc;

import com.publicuhc.commands.routing.DefaultRouter;
import com.publicuhc.configuration.DefaultConfigurator;
import com.publicuhc.testplugins.TestPluginDefaultModules;
import com.publicuhc.testplugins.TestPluginExtraModules;
import com.publicuhc.testplugins.TestPluginReplaceModules;
import com.publicuhc.translate.DefaultTranslate;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPluginLoader;
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
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("bin/test/plugins/testplugin");

        PluginLogger logger = mock(PluginLogger.class);
        whenNew(PluginLogger.class).withAnyArguments().thenReturn(logger);

        TestPluginDefaultModules plugin = new TestPluginDefaultModules(loader, server, pdf, file1, file1) { };

        assertThat(plugin.getConfigurator(), is(nullValue()));
        assertThat(plugin.getRouter(), is(nullValue()));
        assertThat(plugin.getTranslate(), is(nullValue()));

        plugin.onLoad();

        assertThat(plugin.getConfigurator(), is(instanceOf(DefaultConfigurator.class)));
        assertThat(plugin.getTranslate(), is(instanceOf(DefaultTranslate.class)));
        assertThat(plugin.getRouter(), is(instanceOf(DefaultRouter.class)));
    }

    @Test
    public void testUseExtraModules() throws Exception {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("bin/test/plugins/testplugin");

        PluginLogger logger = mock(PluginLogger.class);
        whenNew(PluginLogger.class).withAnyArguments().thenReturn(logger);

        TestPluginExtraModules plugin = new TestPluginExtraModules(loader, server, pdf, file1, file1);

        assertThat(plugin.getConfigurator(), is(nullValue()));
        assertThat(plugin.getRouter(), is(nullValue()));
        assertThat(plugin.getTranslate(), is(nullValue()));

        assertThat(plugin.i, is(nullValue()));

        plugin.onLoad();

        assertThat(plugin.getConfigurator(), is(instanceOf(DefaultConfigurator.class)));
        assertThat(plugin.getTranslate(), is(instanceOf(DefaultTranslate.class)));
        assertThat(plugin.getRouter(), is(instanceOf(DefaultRouter.class)));

        assertThat(plugin.i, is(instanceOf(TestPluginExtraModules.TestConcrete.class)));
    }

    @Test
    public void testReplaceModules() throws Exception {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("bin/test/plugins/testplugin");

        PluginLogger logger = mock(PluginLogger.class);
        whenNew(PluginLogger.class).withAnyArguments().thenReturn(logger);

        TestPluginReplaceModules plugin = new TestPluginReplaceModules(loader, server, pdf, file1, file1);

        assertThat(plugin.getConfigurator(), is(nullValue()));
        assertThat(plugin.getRouter(), is(nullValue()));
        assertThat(plugin.getTranslate(), is(nullValue()));
        assertThat(plugin.builder, is(nullValue()));
        assertThat(plugin.locale, is(nullValue()));

        plugin.onLoad();

        assertThat(plugin.getConfigurator(), is(instanceOf(TestPluginReplaceModules.TestConcreteConfigurator.class)));
        assertThat(plugin.getTranslate(), is(instanceOf(TestPluginReplaceModules.TestConcreteTranslate.class)));
        assertThat(plugin.getRouter(), is(instanceOf(TestPluginReplaceModules.TestConcreteRouter.class)));
        assertThat(plugin.locale, is(equalTo("test.locale")));
        assertThat(plugin.builder, is(instanceOf(TestPluginReplaceModules.TestConcreteCommandRequestBuilder.class)));
    }

}
