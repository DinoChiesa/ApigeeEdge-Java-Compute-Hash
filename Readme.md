# Hash callout

This directory contains the Java source code for
a Java callout for Apigee Edge that does Verification of Hashes, such as MD5, and SHA{256,384,512}.

## License

This material is Copyright 2017-2019 Google LLC
and is licensed under the [Apache 2.0 License](LICENSE). This includes the Java code as well as the API Proxy configuration.

## Disclaimer

This example is not an official Google product, nor is it part of an
official Google product.

## Usage

1. unpack (if you can read this, you've already done that).

2. run the build:
```
 mvn clean package
```

3. if you edit proxy bundles offline, copy the resulting jar file, available in  out/edge-callout-hash-1.0.3.jar to your apiproxy/resources/java directory.  If you don't edit proxy bundles offline, upload the jar file into the API Proxy via the Edge API Proxy Editor .

5. include TWO Java callout policies in your
   apiproxy/resources/policies directory. One will do computation of hashes,
   another will do verification of hashes. The latter is just a computation followed
   by a comparison.

   Example:
   ```xml
   <JavaCallout name="Java-HashCompute">
     <Properties>
       <Property name='debug'>true</Property>
       <Property name='apply-templates'>true</Property>
       <Property name='algorithm'>{request.queryparam.algorithm}</Property>
       <Property name='string-to-hash'>{request.content}</Property>
     </Properties>
     <ClassName>com.dinochiesa.edgecallouts.HashGeneratorCallout</ClassName>
     <ResourceURL>java://edge-callout-hash-1.0.3.jar</ResourceURL>
   </JavaCallout>
   ```

   The second should look like this:
   ```xml
   <JavaCallout name="Java-HashVerify">
     <Properties>
       <Property name='debug'>true</Property>
       <Property name='apply-templates'>true</Property>
       <Property name='algorithm'>{request.queryparam.algorithm}</Property>
       <Property name='expected-hash'>{request.queryparam.expectedhash}</Property>
       <Property name='expected-hash-format'>hex</Property>
       <Property name='string-to-hash'>{request.content}</Property>
     </Properties>
     <ClassName>com.dinochiesa.edgecallouts.HashGeneratorCallout</ClassName>
     <ResourceURL>java://edge-callout-hash-1.0.3.jar</ResourceURL>
   </JavaCallout>
   ```

5. use the Edge UI, or a command-line tool like [importAndDeploy.js](https://github.com/DinoChiesa/apigee-edge-js/blob/master/examples/importAndDeploy.js) or similar to
   import the example proxy into an Edge organization, and then deploy the proxy .

6. Use a client to generate and send http requests with a payload, to the proxy.  Example:
   ```
    curl -i -H 'content-type: text/plain' -X POST \
       'http://ORGNAME-ENVNAME.apigee.net/hash-trial/compute?algorithm=sha256' \
       -d 'The quick brown fox jumped over the lazy dog.'
   ```


## Notes

There is one callout class, com.dinochiesa.edgecallouts.HashGeneratorCallout .

The policy is configured via properties set in the policy XML.  You can set these properties:


| property name     | status    | description                               |
| ----------------- |-----------|-------------------------------------------|
| algorithm         | Required  | SHA-{1,224,256,384,512}, or MD-5 |
| string-to-hash    | Required  | the thing to hash. |
| apply-templates   | Optional  | Whether to treat the source as a template to be resolved. Defaults to false. |
| expected-hash     | Optional  | If you want the policy to check the computed ]hash against an expected value, set this. |
| expected-hash-format | Optional  | Valid values: hex, base64. Default: base64 |


The output variables are:
* `hash_hash-hex`
* `hash_hash-b64`

These represent the computed hash. They are the same value, just encoded
differently.


## Dependencies

* Apigee Edge expressions v1.0
* Apigee Edge message-flow v1.0
* Apache commons lang 2.6
* Apache commons codec 1.7


These jars must be available on the classpath for the compile to
succeed. The build.sh script should download all of these files for
you, automatically. You could also create a Gradle or maven pom file as
well.

If you want to download them manually:

* The first 2 jars are available in Apigee Edge. The first two are
  produced by Apigee; contact Apigee support to obtain these jars to allow
  the compile, or get them here:
  https://github.com/apigee/api-platform-samples/tree/master/doc-samples/java-cookbook/lib

* The Apache jars are also all available in Apigee Edge at runtime. To download them for compile time, you can get them from maven.org.
