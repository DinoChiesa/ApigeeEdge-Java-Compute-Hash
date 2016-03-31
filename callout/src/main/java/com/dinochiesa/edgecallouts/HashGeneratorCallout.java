package com.dinochiesa.edgecallouts;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.IOIntensive;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dinochiesa.edgecallouts.util.TemplateString;
import com.dinochiesa.edgecallouts.util.CalloutUtil;

@IOIntensive
public class HashGeneratorCallout implements Execution {
    private static final String varprefix = "hash_";
    private static final String defaultAlgorithm = "SHA-256";
    private static final String TRUE = "true";

    private static Pattern algMd5Pattern = Pattern.compile("^(MD)-?(5)$", Pattern.CASE_INSENSITIVE);
    private static Pattern algShaPattern = Pattern.compile("^(SHA)-?(1|224|256|384|512)$", Pattern.CASE_INSENSITIVE);

    private final Map<String,String> properties;
    private static String varName(String s) {return varprefix + s;}
    public HashGeneratorCallout(Map properties) {
        this.properties = CalloutUtil.genericizeMap(properties);
    }

    private boolean getWantApplyTemplates() {
        String prop = properties.get("apply-templates");
        if (prop != null) {
            prop = prop.toLowerCase();
            if (prop.equals("true") || prop.equals("yes")) {
                return true;
            }
        }
        return false;
    }

    private String getStringToHash(MessageContext msgCtxt) throws Exception {
        String msg = this.properties.get("string-to-hash");
        if (msg == null || msg.equals("")) {
            // by default, get the content of the message (either request or response)
            return msgCtxt.getVariable("message.content");
        }

        if (!getWantApplyTemplates()) {
            return msg;
        }

        // Replace ALL curly-braced items in the string-to-sign
        // This is like resolveVariableFromContext on steroids.
        TemplateString ts = new TemplateString(msg);
        Map<String,String> valuesMap = new HashMap<String,String>();
        for (String s : ts.variableNames) {
            valuesMap.put(s, (String) msgCtxt.getVariable(s));
        }
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String resolvedString = sub.replace(ts.template);
        return resolvedString;
    }

    private String getExpectedHash(MessageContext msgCtxt) throws Exception {
        String hash =  this.properties.get("expected-hash");
        if (hash == null || hash.equals("")) {
            return null;
        }
        hash = CalloutUtil.resolveVariableFromContext(hash, msgCtxt);
        if (hash == null || hash.equals("")) {
            throw new IllegalStateException("expected-hash resolves to null or empty.");
        }
        return hash.trim();
    }

    private String getExpectedHashFormat(MessageContext msgCtxt) throws Exception {
        String format =  this.properties.get("expected-hash-format");
        if (format == null || format.equals("")) {
            return "base64";
        }
        format = CalloutUtil.resolveVariableFromContext(format, msgCtxt);
        if (format == null || format.equals("")) {
            throw new IllegalStateException("expected-hash-format resolves to null or empty.");
        }
        return format.trim();
    }

    private boolean getDebug(MessageContext msgCtxt) throws Exception {
        String flag = this.properties.get("debug");
        if (flag == null || flag.equals("")) {
            return false;
        }
        flag = CalloutUtil.resolveVariableFromContext(flag, msgCtxt);
        if (flag == null || flag.equals("")) {
            return false;
        }
        return flag.equalsIgnoreCase(TRUE);
    }

    private String getAlgorithm(MessageContext msgCtxt) throws Exception {
        String alg = (String) this.properties.get("algorithm");
        if (alg == null || alg.equals("")) {
            return defaultAlgorithm;
        }
        alg = CalloutUtil.resolveVariableFromContext(alg, msgCtxt);
        if (alg == null || alg.equals("")) {
            return defaultAlgorithm;
        }
        return alg;
    }

    private static String javaizeAlgorithmName(MessageContext msgCtxt,String alg)
        throws IllegalStateException {
        Matcher m = algShaPattern.matcher(alg);
        if (!m.matches()) {
            m = algMd5Pattern.matcher(alg);
            if (!m.matches()) {
                throw new IllegalStateException("the algorithm name (" + alg + ") is not recognized");
            }
        }
        String group = m.group(1).toUpperCase();
        String stdName = (group.startsWith("SHA")) ? group + '-' + m.group(2) :
            group + m.group(2) ;
        return stdName;
    }

    private void clearVariables(MessageContext msgCtxt) {
        msgCtxt.removeVariable(varName("error"));
        msgCtxt.removeVariable(varName("stacktrace"));
        msgCtxt.removeVariable(varName("javaizedAlg"));
        msgCtxt.removeVariable(varName("alg"));
        msgCtxt.removeVariable(varName("string-to-hash"));
        msgCtxt.removeVariable(varName("hash-hex"));
        msgCtxt.removeVariable(varName("hash-b64"));
    }

    public ExecutionResult execute(MessageContext msgCtxt,
                                   ExecutionContext exeCtxt) {
        try {
            clearVariables(msgCtxt);
            String stringToHash = getStringToHash(msgCtxt);
            String algorithm = getAlgorithm(msgCtxt);
            boolean debug = getDebug(msgCtxt);
            if (debug) {
                msgCtxt.setVariable(varName("algorithm"), algorithm);
            }
            String javaizedAlg = javaizeAlgorithmName(msgCtxt, algorithm);
            if (debug) {
                msgCtxt.setVariable(varName("javaizedAlg"), javaizedAlg);
            }

            MessageDigest md = MessageDigest.getInstance(javaizedAlg);
            byte[] hashBytes = md.digest(stringToHash.getBytes("UTF-8"));
            String hashHex = Hex.encodeHexString(hashBytes);
            String hashB64 = Base64.encodeBase64String(hashBytes);

            msgCtxt.setVariable(varName("string-to-hash"), stringToHash);
            msgCtxt.setVariable(varName("hash-hex"), hashHex);
            msgCtxt.setVariable(varName("hash-b64"), hashB64);

            // presence of expected-hash property indicates verification wanted
            String expectedHash = getExpectedHash(msgCtxt);
            if (expectedHash !=null) {
                // want verification
                String expectedHashFormat = getExpectedHashFormat(msgCtxt);
                if (expectedHashFormat.toLowerCase().equals("base64")) {
                    if (debug) {
                        System.out.printf("expected(%s) computed(%s)\n", expectedHash, hashB64);
                    }
                    if (!hashB64.equals(expectedHash)) {
                        msgCtxt.setVariable(varName("error"), "The hash or digest does not verify");
                        return ExecutionResult.ABORT;
                    }
                }
                else if (expectedHashFormat.toLowerCase().equals("hex")) {
                    if (debug) {
                        System.out.printf("expected(%s) computed(%s)\n", expectedHash, hashHex);
                    }
                    if (!hashHex.toLowerCase().equals(expectedHash.toLowerCase())) {
                        msgCtxt.setVariable(varName("error"), "The hash or digest does not verify");
                        return ExecutionResult.ABORT;
                    }
                }
                else {
                    msgCtxt.setVariable(varName("error"), "The expected hash format is not recognized");
                    return ExecutionResult.ABORT;
                }
            }
        }
        catch (Exception e){
            msgCtxt.setVariable(varName("error"), e.getMessage());
            msgCtxt.setVariable(varName("stacktrace"), ExceptionUtils.getStackTrace(e));
            return ExecutionResult.ABORT;
        }
        return ExecutionResult.SUCCESS;
    }
}
