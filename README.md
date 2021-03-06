#PluginFramework

[![Build Status](https://travis-ci.org/Eluinhost/pluginframework.svg?branch=master)](https://travis-ci.org/Eluinhost/pluginframework)

This is a framework used to give extra features to [Bukkit](http://www.bukkit.org/ "Bukkit") plugins.

##[Usage + Documentation](docs/Usage.md)

**DOCUMENTATION IN PROGRESS, NOT COMPLETE**

##Features

###Commands

The main focus of the framework. Allows registering commands by passing a class/instance to the router like events.
All command classes allow for dependency injection.

####Example

    //simplest version
    @CommandMethod("test")
    public void echoCommand(OptionSet set, CommandSender sender) {
        sender.sendMessage("test command ran");
    }

    //version will all the features
    @CommandMethod(value = "test", helpOption = "h")
    @CommandOptions({"a", "z", "[arguments]"})
    @PermissionRestriction(value = {"TEST.PERMISSION.1", "TEST.PERMISSION.2"}, needsAll = false)
    @SenderRestriction({Player.class, SubClassPlayer.class})
    public void method(OptionSet set, Player player, Double avalue, Location target, List<String> arguments)
    {
        //check teams
        if(set.has("teams")) ...
    }

    @OptionsMethod
    public void method(OptionDeclarer declarer)
    {
        declarer.accepts("z")
                .withOptionalArg()
                .required()
                .withValuesConvertedBy(new LocationValueConverter());
        declarer.accepts("a")
                .withRequiredArg()
                .ofType(Double.class);
        declarer.accepts("teams");
    }

Commands can define sender restrictions, permission restrictions, options e.t.c. for the documentation read the
[Command Documentation](docs/Commands.md)

###Configuration

Allows you to get config files by an ID and will write them to the data directory automatically when first accessed.
Allows for saving/reloading files by ID too.

####Example
    //inside the plugin main class
    Configurator configurator = getConfigurator();

    //get the config file at /subfolder/configFile.yml
    //will save it to data directory with default if it doesn't already exist
    configurator.getConfig("subfolder:configFile");

###Translate

Allows for the translation of messages based on different locales based on the sender.
The framework adds a config file to change the locale for the console, remote console, command blocks.
Broadcasts send messages to all players in their own locales.
It fetches the locale per played based on their ingame settings and selects the most relevant translation for their locale
based on ResourceBundle

Extra parameters are passed into String.format on the key provided.

####Example

    //returns translated message for the sender
    translate.translate("message", sender);

    //returns translated message for the Locale, with 1 var
    translate.translate("message", Locale.FRENCH, 20);

    //send a translated message
    translate.sendMessage("translation.message", sender);

    //send a translated message with 1 variable
    translate.translate("translation.message", sender, "variable");
    
    //works with broadcasts
    translate.broadcastMessage("translation.message");

    //broadcast for permission
    translate.broadcastMessageForPermission("translation.message", "TEST.PERMISSION");

[Translate Documentation](docs/Translations.md)

###Dependency Injection

Allows for the plugin class to be injected with it's dependencies. All registered command classes are also injected.
Uses Google Guice. Allows to supply new/override existing bindings within the plugin creation

####Example
    //main plugin class does NOT get constructor injection and only get's injected just before onFrameworkEnable is called
    private CustomInterface interface;

    @Inject
    private void setCustomInterface(CustomInterface ci) {
        interface = ci;
    }

    public void initialModules(List<Module> modules) {
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CustomInterface.class).to(CustomConcrete.class);
            }
        });
        modules.addAll(getDefaultModules());
    }
