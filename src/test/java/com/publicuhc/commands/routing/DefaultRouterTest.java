package com.publicuhc.commands.routing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.exceptions.InvalidReturnTypeException;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.CommandRequestBuilder;
import com.publicuhc.commands.requests.DefaultCommandRequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class DefaultRouterTest {

    private DefaultRouter router;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws NoSuchMethodException {
       Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Router.class).to(DefaultRouter.class);
                bind(CommandRequestBuilder.class).to(DefaultCommandRequestBuilder.class);
            }
        });
        router = (DefaultRouter) injector.getInstance(Router.class);
    }

    @After
    public void teardown(){
        router = null;
    }

    @Test
    public void testValidTabComplete() throws NoSuchMethodException, InvalidReturnTypeException {
        Method validTabComplete = TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(validTabComplete);
    }

    @Test
    public void testVoidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onTabCompleteNoReturn", CommandRequest.class);
        thrown.expect(InvalidReturnTypeException.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test
    public void testInvalidTypeTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidReturnTypeTabComplete", CommandRequest.class);
        thrown.expect(InvalidReturnTypeException.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test
    public void testInvalidGenericReturnTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidGenericReturnTypeTabComplete", CommandRequest.class);
        thrown.expect(InvalidReturnTypeException.class);
        router.checkTabCompleteReturn(invalidReturn);
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
        public List<String> onMissingAnnotation(CommandRequest request) {
            return null;
        }
    }
}
