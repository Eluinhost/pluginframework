Commands
========

You can register commands by passing a class/instance to the Router.

Commands are triggered by reflection and allow for dependency injection.

Register command
----------------

To register a command use the Router object. You can get a router object via @Inject or by
using the getRouter method in the main plugin class (after it has been loaded).

    @Inject
    public void getRouter(Router router)
    {
        router.registerCommands(CommandClass.class);
    }

You can register a class and it will be created by the framework and have constructor/setter injectors
completed and then it will be parsed for commands. You can also pass an instance of an object and optionally
inject setter dependencies before it parses for commands.

How to define a command Method
------------------------------

A command method can be defined by adding an `@CommandMethod` annotation to it. A command MUST have this annotation
to be parsed as a command by the router.

    @CommandMethod("test")
    public void testCommand(OptionSet set, CommandSender sender)
    {
        sender.sendMessage("test command ran!");
    }

###@CommandMethod Parameters:

`value` - required

Name of the command to trigger this method on, e.g. `command="test"`. If it contains a space it defines a subcommand;
`command="test subcommand"` will run on the command `test` only if `subcommand` is the first argument of the command.
Any subcommands ('subcommand' in the example above) are removed from the argument list and MUST come before options
in the command.

`helpOption` - optional, default '?'

By default all commands have `-?` to show their options/help. Change this to change the option. e.g. `helpOption = "h"`
would make `-h` show help and leave `-?` free.

All methods that have @CommandMethod are required to fit the following restrictions

- Parameter 1 must be an OptionSet
- Parameter 2 must be a CommandSender (or subclass, see @SenderRestriction)
- Must not have any more arguments (unless @CommandOptions is present)

Restrict by permissions
-----------------------

You can add permission restriction by adding a `@PermissionRestriction` annotation to the method:

    @CommandMethod("test")
    @PermissionRestriction("TEST.PERMISSION");
    public void testCommand(OptionSet set, CommandSender sender)
    {
        request.sendMessage("test command ran!");
    }

###@PermissionRestriction Parameters

`value` - required

List of all the permissions to apply to this method

`needsAll` - optional, default true

Tells the router whether the route needs all of the permissions to match or just one

Restrict by sender type
-----------------------

You can add sender type restriction by adding a `@SenderRestriction` annotation to the method:

    @CommandMethod("test")
    @SenderRestriction(Player.class);
    public void testCommand(OptionSet set, Player player)
    {
        request.sendMessage("test command ran!");
    }

When a sender restriction is in place the second parameter is allowed to be changed to any class that can be applied
to all of the chosen sender types.

###@SenderRestriction Parameters

`value` - required

List of sender classes that are allowed to trigger this command. Subclasses of provided classes are also allowed.
(i.e. If set to CommandSender.class all sender types are allowed)

Add options
-----------

You can add options to a command by adding the options annotation `@CommandOptions` and creating a method with the
same name ('testCommand') and with a single parameter `OptionDeclarer`. The `@OptionsMethod` annotation is optional
and can be used for clarity. This method is called when registering the command to specify the options for the command.

    //inside a 'command' class
    @CommandMethod("test")
    @CommandOptions({"prefix", "[arguments]"})
    public void testCommand(OptionSet set, CommandSender sender, String pre, List<Location> locations)
    {
        if(set.has("u"))
            //make uppercase

        sender.sendMessage(....);
    }

    @OptionsMethod
    public void testCommand(OptionDeclarer parser)
    {
        parser.accepts("u", "Print as upper case");
        parser.accepts("prefix")
                .withRequiredArg()
                .describedAs("Prefix to show before the message");
        parser.nonOptions()
                .withValuesConvertedBy(new LocationValueConverter())
                .describedAs("some locations");
    }

###@CommandOptions Parameters

`value` - required, default {}

List of all of the options we want to use in the method signature. Arguments are in order AFTER the CommandSender. The
types of the parameters are checked for validity when registering the command. The `[arguments]` identifier is a special
case for the nonOptions and should always be of type List.

The above example has the following options:

- `-u` - an optional flag we can check with the OptionSet
- `--prefix=xxx` / `--prefix xxx` - adds a prefix before the message, optional, requires an argument if provided
- `all non options` - parsed using a ValueConverter.

For more information on option parser and what is possible check out the
[jopt-simple documentation](http://pholser.github.io/jopt-simple/examples.html)