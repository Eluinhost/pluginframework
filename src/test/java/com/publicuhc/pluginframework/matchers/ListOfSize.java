package com.publicuhc.pluginframework.matchers;

import org.mockito.ArgumentMatcher;

import java.util.List;

public class ListOfSize extends ArgumentMatcher<List> {

    private final int size;

    public ListOfSize(int size)
    {
        this.size = size;
    }

    @Override
    public boolean matches(Object argument) {
        return argument instanceof List && ((List) argument).size() == size;
    }
}
