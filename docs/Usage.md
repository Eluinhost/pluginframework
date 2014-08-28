Usage
=====

To use the framework first make your plugin class extend FrameworkJavaPlugin:

    public class TestPlugin extends FrameworkJavaPlugin
    {
        @Override
        protected void initialModules(List<Module> modules)
        {

        }
    }

This will force you to implement `initialModules`. This method sets up the dependency injection for your plugin using
GUICE modules. To use the default bindings and include all the modules you can just do the following:

    @Override
    protected void initialModules(List<Module> modules)
    {
        modules.addAll(getDefaultModules());
    }

This will enable all the features of the framework.

Alternatively you can enable just the features you want to use:

    @Override
    protected void initialModules(List<Module> modules)
    {
        modules.add(new PluginModule(this));
        modules.add(new RouterModule());
    }

Make sure to always include the PluginModule.

List of all modules and their functions:

//TODO documentation links

- ConfigurationModule - adds configuration framework
- MetricsModule - adds plugin metrics
- RoutingModule - adds routing/command framework
- TranslateModule - adds the translation framework
- PluginModule - must be included, adds bindings for loggers/file system/plugin instance

You can add your own modules to the list to get your own dependency injection to initialize your plugin.

To override any framework bindings you can do something like the following to override certain bindings:

    @Override
    protected void initialModules(List<Module> modules)
    {
        modules.add(new PluginModule(this));
        modules.add(Modules.override(new RoutingModule()).with(new AbstractModule() {
            @Override
            protected void configure()
            {
                bind(RoutingMethodParser.class).to(NewRoutingMethodParser.class);
            }
        }));
    }

The above overrides the method parser but leaves the router binding alone.

This then allows you to use @Inject annotations to initialize the plugin. The main plugin class will NOT get constructor
injection but will receive parameter and setter injection. For more information on DI check the GUICE documentation.