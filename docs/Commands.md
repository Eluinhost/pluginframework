Commands
========

You can register commands by passing a class/instance to the Router.

Commands are triggered by reflection and allow for dependency injection.

Register command
----------------

To register a command use the Router object. You can get a router object via @Inject or by
using the getRouter method in the main plugin class (after it has been loaded).

You can register a class and it will be created by the framework and have constructor/setter injectors
completed and then it will be parsed for commands. You can also pass an instance of an object and optionally
inject setter dependencies before it parses for commands.

Basic Method Style
------------------

This kind of method allows for registering a command without any options with string arguments

    //inside a 'command' class
    @CommandMethod(command = "test")
    public void testCommand(CommandRequest request)
    {
        request.sendMessage("test command");
    }

The above command will run on `/test` and will print `test command` on running.

Restrict by permissions
-----------------------

You can add permission restrictions by adding the permission to the annotation:

    //inside a 'command' class
    @CommandMethod(command = "test", permission = "test.permission")
    public void testCommand(CommandRequest request)
    {
        request.sendMessage("test command");
    }

Now the command `/test` will only run for people with the permission `test.permission` and will show a no
permission error for people without it.

Add options
-----------

You can add options to a command by adding the options toggle on the annotation and adding another method:

    //inside a 'command' class
    @CommandMethod(command = "test", options = true)
    public void testCommand(CommandRequest request)
    {

        OptionSet set = request.getOptions();
        List<String> nonOptions = set.nonOptions();
        String message = ...merge all nonoption arguments...;

        if(set.has("u"))
            message = message.toUpperCase();

        if(set.has("prefix"))
            message = (String) set.valueOf("prefix") + message;

        Player p = (Player) set.valueOf("p");

        p.sendMessage(message);
    }

    @OptionsMethod
    public void testCommand(OptionParser parser)
    {
        parser.accepts("u", "Print as upper case");

        parser.accepts("prefix")
                .withRequiredArg()
                .describedAs("Prefix to show before the message");

        parser.accepts("p")
                .withRequiredArg()
                .required()
                .withValuesConvertedBy(new OnlinePlayerValueConverter())
                .describedAs("Player to send to");
    }

The @OptionsMethod annotation is optional and can be used for clarity. To add options you must add the options = true
to the annotation and add a method of the same name that accepts an `OptionParser` as its only argument.

This method is called when registering the command to specify the options for the command.

The above example has the following options:

- `-u` - prints the message as uppercase, optional
- `--prefix=xxx` / `--prefix xxx` - adds a prefix before the message, optional, requires an argument
- `-p=player` / `-p player` - the player to send to, required (converts by player name)

Examples:

`/test -p ghowden --prefix=000 a message` = sends ghowden the message `000a message`

`/test ghowden --prefix=000 a message` = sends help message because -p is required

`/test -u -p eluinhost a message` = sends eluinhost the message `A MESSAGE`

For more information on option parser and what is possible check out the [jopt-simple documentation](http://pholser.github.io/jopt-simple/examples.html)