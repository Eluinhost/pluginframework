package com.publicuhc.pluginframework;

public class TestObject
{
    public static final String TEST_STRING = "TEST STRING";

    public String getNoArg()
    {
        return TEST_STRING;
    }

    public String getArg(String arg)
    {
        return arg;
    }
}