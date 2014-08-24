package com.publicuhc.pluginframework.matchers;

import java.util.List;

import static org.mockito.Matchers.argThat;

public class UHCMatchers {

    public static List listOfSize(int size)
    {
        return argThat(new ListOfSize(size));
    }
}
