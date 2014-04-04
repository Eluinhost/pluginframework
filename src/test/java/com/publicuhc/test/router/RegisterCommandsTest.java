package com.publicuhc.test.router;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.publicuhc.commands.CommandModule;
import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.exceptions.CommandClassParseException;
import com.publicuhc.commands.exceptions.InvalidMethodParametersException;
import com.publicuhc.commands.exceptions.InvalidReturnTypeException;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.routing.Router;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RegisterCommandsTest {

    @Test(expected = InvalidMethodParametersException.class)
    public void testWrongCommandMethodParams() throws CommandClassParseException {
        Injector injector = Guice.createInjector(new CommandModule());
        Router router = injector.getInstance(Router.class);
        router.registerCommands(FailCommandParameters.class);
    }

    @Test(expected = InvalidMethodParametersException.class)
    public void testWrongTabCompleteParams() throws CommandClassParseException {
        Injector injector = Guice.createInjector(new CommandModule());
        Router router = injector.getInstance(Router.class);
        router.registerCommands(FailCommandParameters.class);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testWrongTabCompleteReturnParams() throws CommandClassParseException {
        Injector injector = Guice.createInjector(new CommandModule());
        Router router = injector.getInstance(Router.class);
        router.registerCommands(FailTabCompleteReturnType.class);
    }

    //TODO missing routeinfo | command + tab

    //TODO invalid return routeinfo

    //TODO missing base command | command + tab

    //TODO missing annotation routeinfo

    private static class FailCommandParameters {
        @CommandMethod
        public void failCommand() {} //incorrect parameter
    }

    private static class FailTabCompleteParameters {
        @TabCompletion
        public void failCommand() {} //incorrect parameter
    }

    private static class FailTabCompleteReturnType {
        @TabCompletion
        public void failCommand(CommandRequest request) {} //incorrect return type
    }
}
