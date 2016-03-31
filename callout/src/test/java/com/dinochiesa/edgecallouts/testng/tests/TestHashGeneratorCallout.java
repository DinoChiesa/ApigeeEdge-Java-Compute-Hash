package com.dinochiesa.edgecalluts.testng.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeMethod;

import mockit.Mock;
import mockit.MockUp;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.message.MessageContext;
import com.apigee.flow.execution.ExecutionResult;

import com.dinochiesa.edgecallouts.HashGeneratorCallout;

public class TestHashGeneratorCallout {

    MessageContext msgCtxt;
    ExecutionContext exeCtxt;

    @BeforeMethod()
    public void testSetup1() {

        msgCtxt = new MockUp<MessageContext>() {
                private Map<String,Object> variables;
                public void $init() {
                    getVariables();
                }
                private Map<String,Object> getVariables() {
                    if (variables == null) {
                        variables = new HashMap<String,Object>();
                    }
                    return variables;
                }
                @Mock()
                public Object getVariable(final String name){
                    return getVariables().get(name);
                }

                @Mock()
                public boolean setVariable(final String name, final Object value) {
                    getVariables().put(name, (String)value);
                    return true;
                }

                @Mock()
                public boolean removeVariable(final String name) {
                    if (getVariables().containsKey(name)) {
                        variables.remove(name);
                    }
                    return true;
                }
            }.getMockInstance();

        exeCtxt = new MockUp<ExecutionContext>(){ }.getMockInstance();
    }

    @Test()
    public void test1_SHA256_Base64() {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("string-to-hash", "The quick brown fox jumped over the lazy dog.");
        properties.put("algorithm", "sha256");
        properties.put("expected-hash", "aLEoK5HeLAVMNmKcuN1EfxLwltPjxYeXjcIkhERjNIM=");
        properties.put("expected-hash-format", "base64");
        properties.put("debug", "true");

        HashGeneratorCallout callout = new HashGeneratorCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        // retrieve output
        String error = msgCtxt.getVariable("hash_error");
        if (error!=null)
            System.out.println("error: " + error);
            
        // check result and output
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertNull(error);
    }

    @Test()
    public void test1_SHA256_Base64_2() {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("string-to-hash", "The quick brown fox jumped over the lazy dog.");
        properties.put("algorithm", "sha256");
        properties.put("expected-hash", "aLEoK5HeLAVMNmKcuN1EfxLwltPjxYeXjcIkhERjNIM=");
        // properties.put("expected-hash-format", "base64"); // defaults to base64
        properties.put("debug", "true");

        HashGeneratorCallout callout = new HashGeneratorCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        // retrieve output
        String error = msgCtxt.getVariable("hash_error");
        if (error!=null)
            System.out.println("error: " + error);

        // check result and output
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertNull(error);
    }

    @Test()
    public void test1_SHA256_Hex() {
        Map<String,String> properties = new HashMap<String,String>();

        properties.put("string-to-hash", "The quick brown fox jumped over the lazy dog.");
        properties.put("algorithm", "sha256");
        properties.put("expected-hash", "68b1282b91de2c054c36629cb8dd447f12f096d3e3c587978dc2248444633483");
        properties.put("expected-hash-format", "hex");
        properties.put("debug", "true");

        HashGeneratorCallout callout = new HashGeneratorCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        // retrieve output
        String error = msgCtxt.getVariable("hash_error");
        if (error!=null)
            System.out.println("error: " + error);

        // check result and output
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertNull(error);
    }
    
    @Test()
    public void test1_MD5_Hex() {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("string-to-hash", "The quick brown fox jumped over the lazy dog.");
        properties.put("algorithm", "md5");
        properties.put("expected-hash", "5c6ffbdd40d9556b73a21e63c3e0e904");
        properties.put("expected-hash-format", "hex");
        properties.put("debug", "true");
        HashGeneratorCallout callout = new HashGeneratorCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        // retrieve output
        String error = msgCtxt.getVariable("hash_error");
        if (error!=null)
            System.out.println("error: " + error);

        // check result and output
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertNull(error);
    }
    
    @Test()
    public void test1_SHA512_Hex() {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("string-to-hash", "The quick brown fox jumped over the lazy dog.");
        properties.put("algorithm", "sha512");
        properties.put("expected-hash", "0a8c150176c2ba391d7f1670ef4955cd99d3c3ec8cf06198cec30d436f2ac0c9b64229b5a54bdbd5563160503ce992a74be528761da9d0c48b7c74627302eb25");
        properties.put("expected-hash-format", "hex");
        properties.put("debug", "true");
        HashGeneratorCallout callout = new HashGeneratorCallout(properties);
        ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

        // retrieve output
        String error = msgCtxt.getVariable("hash_error");
        if (error!=null)
            System.out.println("error: " + error);

        // check result and output
        Assert.assertEquals(result, ExecutionResult.SUCCESS);
        Assert.assertNull(error);
    }


}
