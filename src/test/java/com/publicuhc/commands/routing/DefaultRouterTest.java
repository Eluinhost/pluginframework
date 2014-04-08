package com.publicuhc.commands.routing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.exceptions.AnnotationMissingException;
import com.publicuhc.commands.exceptions.CommandClassParseException;
import com.publicuhc.commands.exceptions.InvalidMethodParametersException;
import com.publicuhc.commands.exceptions.InvalidReturnTypeException;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.CommandRequestBuilder;
import com.publicuhc.commands.requests.DefaultCommandRequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class DefaultRouterTest {

    private DefaultRouter router;
    private Injector injector;

    @Before
    public void setup() throws NoSuchMethodException {
       injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Router.class).to(DefaultRouter.class);
                bind(CommandRequestBuilder.class).to(DefaultCommandRequestBuilder.class);
                bind(TestInterface.class).to(InjectorTest.class);
            }
        });
        router = (DefaultRouter) injector.getInstance(Router.class);
    }

    @After
    public void teardown(){
        router = null;
    }

    /*####################################
     *  Tests for checkTabCompleteReturn #
     *##################################*/

    @Test
    public void testValidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method validTabComplete = TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(validTabComplete);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testVoidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onTabCompleteNoReturn", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidTypeTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidReturnTypeTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidGenericReturnTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidGenericReturnTypeTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    /*###########################
     *  Tests for isTabComplete #
     *#########################*/

    @Test
    public void testValidTabCompleteAnnotation() throws NoSuchMethodException {
        Method validAnnotation = TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        assertTrue(router.isTabComplete(validAnnotation));
    }

    @Test
    public void testMissingTabCompleteAnnotation() throws NoSuchMethodException {
        Method missingAnnotation = TestCommandClass.class.getMethod("onMissingAnnotationTabComplete", CommandRequest.class);
        assertFalse(router.isTabComplete(missingAnnotation));
    }

    /*#############################
     *  Tests for isCommandMethod #
     *###########################*/

    @Test
    public void testValidCommandMethodAnnotation() throws NoSuchMethodException {
        Method validAnnotation = TestCommandClass.class.getMethod("onValidCommandMethod", CommandRequest.class);
        assertTrue(router.isCommandMethod(validAnnotation));
    }

    @Test
    public void testMissingCommandMethodAnnotation() throws NoSuchMethodException {
        Method missingAnnotation = TestCommandClass.class.getMethod("onMissingAnnotationCommandMethod", CommandRequest.class);
        assertFalse(router.isCommandMethod(missingAnnotation));
    }

    /*############################
     *  Tests for checkRouteInfo #
     *##########################*/

    @Test
    public void testValidRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method validRouteInfo = TestCommandClass.class.getMethod("onValidRouteInfo");
        router.checkRouteInfo(validRouteInfo);
    }

    @Test(expected = AnnotationMissingException.class)
    public void testMissingAnnotationRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onMissingAnnotationRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testMissingReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onMissingReturnRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onInvalidReturnRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    /*#############################
     *  Tests for checkParameters #
     *###########################*/

    @Test
    public void testValidParameters() throws NoSuchMethodException, CommandClassParseException {
        Method valid = TestCommandClass.class.getMethod("onValidCommandMethod", CommandRequest.class);
        router.checkParameters(valid);
    }

    @Test(expected = InvalidMethodParametersException.class)
    public void testInvalidParameters() throws NoSuchMethodException, CommandClassParseException {
        Method invalid = TestCommandClass.class.getMethod("onInvalidParameters", String.class);
        router.checkParameters(invalid);
    }

    //TODO add tests for register commands and command invocation and get proxies
    @Test
    public void testRegisterCommandsInjectionObject() throws CommandClassParseException {
        TestObject object = new TestObject();
        assertThat(object.getTestInterface(), is(nullValue()));

        router.registerCommands(object, true);
        assertThat(object.getTestInterface(), is(not(nullValue())));
        assertThat(object.getTestInterface().getMessage(), is(equalTo("SUCCESS")));
    }

    @Test
    public void testRegisterCommandsInjectionClass() throws CommandClassParseException {
        Object object = router.registerCommands(TestObject.class);
        assertTrue(object instanceof TestObject);
        TestObject testObject = (TestObject) object;
        assertThat(testObject.getTestInterface(), is(not(nullValue())));
        assertThat(testObject.getTestInterface().getMessage(), is(equalTo("SUCCESS")));
    }

    @SuppressWarnings("unused")
    private static class TestCommandClass {

        /**
         * Test for checking invalid return type
         * @param request n/a
         */
        @TabCompletion
        public void onTabCompleteNoReturn(CommandRequest request) {

        }

        /**
         * Test for checking invalid parameters
         * @return n/a
         */
        @TabCompletion
        public List<String> onTabCompleteNoParam() {
            return null;
        }

        /**
         * Test for checking a valid method
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<String> onValidTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public String onInvalidReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<Integer> onInvalidGenericReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking missing annotation
         * @param request n/a
         * @return n/a
         */
        public List<String> onMissingAnnotationTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for a valid command method
         * @param request n/a
         */
        @CommandMethod
        public void onValidCommandMethod(CommandRequest request) {
        }

        /**
         * Test for invalid parameters
         * @param string n/a
         */
        @CommandMethod
        public void onInvalidParameters(String string) {
        }

        /**
         * Test for missing annotation
         * @param request n/a
         */
        public void onMissingAnnotationCommandMethod(CommandRequest request) {
        }

        /**
         * Test for a valid route info
         * @return n/a
         */
        @RouteInfo
        public MethodRoute onValidRouteInfo() {
            return null;
        }

        /**
         * Test for missing annotation
         * @return n/a
         */
        public MethodRoute onMissingAnnotationRouteInfo() {
            return null;
        }

        /**
         * Test for missing return type
         */
        @RouteInfo
        public void onMissingReturnRouteInfo() {
        }

        /**
         * Test for invalid return type
         * @return n/a
         */
        @RouteInfo
        public String onInvalidReturnRouteInfo() {
            return null;
        }
    }

    private static class TestObject {

        private TestInterface testInterface = null;

        @Inject
        public void setInterface(TestInterface testInterface){
            this.testInterface = testInterface;
        }

        public TestInterface getTestInterface() {
            return testInterface;
        }
    }

    private static interface TestInterface {
        String getMessage();
    }

    private static class InjectorTest implements TestInterface {
        public String getMessage() {
            return "SUCCESS";
        }
    }
}
