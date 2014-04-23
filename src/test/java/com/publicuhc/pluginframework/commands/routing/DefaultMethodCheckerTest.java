/*
 * DefaultMethodCheckerTest.java
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

package com.publicuhc.pluginframework.commands.routing;

import com.publicuhc.pluginframework.commands.exceptions.AnnotationMissingException;
import com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException;
import com.publicuhc.pluginframework.commands.exceptions.InvalidMethodParametersException;
import com.publicuhc.pluginframework.commands.exceptions.InvalidReturnTypeException;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.logging.Logger;

@RunWith(PowerMockRunner.class)
public class DefaultMethodCheckerTest {

    private DefaultMethodChecker checker;

    @Before
    public void onSetUp() {
        checker = new DefaultMethodChecker(Logger.getAnonymousLogger());
    }

    @Test
    public void testValidTabComplete() throws NoSuchMethodException, InvalidReturnTypeException, AnnotationMissingException, InvalidMethodParametersException {
        Method validTabComplete = DefaultRouterTest.TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        checker.checkTabComplete(validTabComplete);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testVoidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException, AnnotationMissingException, InvalidMethodParametersException {
        Method invalidReturn = DefaultRouterTest.TestCommandClass.class.getMethod("onTabCompleteNoReturn", CommandRequest.class);
        checker.checkTabComplete(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidTypeTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException, AnnotationMissingException, InvalidMethodParametersException {
        Method invalidReturn = DefaultRouterTest.TestCommandClass.class.getMethod("onInvalidReturnTypeTabComplete", CommandRequest.class);
        checker.checkTabComplete(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidGenericReturnTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException, AnnotationMissingException, InvalidMethodParametersException {
        Method invalidReturn = DefaultRouterTest.TestCommandClass.class.getMethod("onInvalidGenericReturnTypeTabComplete", CommandRequest.class);
        checker.checkTabComplete(invalidReturn);
    }

    @Test
    public void testValidRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method validRouteInfo = DefaultRouterTest.TestCommandClass.class.getMethod("onValidRouteInfo", RouteBuilder.class);
        checker.checkRouteInfo(validRouteInfo);
    }

    @Test(expected = AnnotationMissingException.class)
    public void testMissingAnnotationRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = DefaultRouterTest.TestCommandClass.class.getMethod("onMissingAnnotationRouteInfo", RouteBuilder.class);
        checker.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testMissingReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = DefaultRouterTest.TestCommandClass.class.getMethod("onMissingReturnRouteInfo", RouteBuilder.class);
        checker.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = DefaultRouterTest.TestCommandClass.class.getMethod("onInvalidReturnRouteInfo", RouteBuilder.class);
        checker.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidMethodParametersException.class)
    public void testInvalidParameters() throws NoSuchMethodException, CommandClassParseException {
        Method invalid = DefaultRouterTest.TestCommandClass.class.getMethod("onInvalidParameters", String.class);
        checker.checkCommandMethod(invalid);
    }

}
