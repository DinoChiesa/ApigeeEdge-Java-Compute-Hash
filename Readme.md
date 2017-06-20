# Hash callout

This directory contains the Java source code for 
a Java callout for Apigee Edge that does Verification of Hashes, such as MD5, and SHA{256,384,512}.

## License

This material is copyright 2015,2016 Apigee Corporation, 2017 Google Inc.
and is licensed under the [Apache 2.0 License](LICENSE). This includes the Java code as well as the API Proxy configuration. 


## Usage:

1. unpack (if you can read this, you've already done that).

2. run the build: 
```
 mvn clean package 
```

3. if you edit proxy bundles offline, copy the resulting jar file, available in  out/edge-callout-hash-1.0.2.jar to your apiproxy/resources/java directory.  If you don't edit proxy bundles offline, upload the jar file into the API Proxy via the Edge API Proxy Editor . 

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
     <ResourceURL>java://edge-callout-hash-1.0.2.jar</ResourceURL>
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
     <ResourceURL>java://edge-callout-hash-1.0.2.jar</ResourceURL>
   </JavaCallout>
   ```
   
5. use the Edge UI, or a command-line tool like [pushapi](https://github.com/carloseberhardt/apiploy) or similar to
   import the proxy into an Edge organization, and then deploy the proxy . 

6. Use a client to generate and send http requests with a payload, to the proxy.  Example:
   ```
    curl -i -H 'content-type: text/plain' -X POST \
       'http://ORGNAME-ENVNAME.apigee.net/hash-trial/compute?algorithm=sha256' \
       -d 'The quick brown fox jumped over the lazy dog.'
   ```


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

    The first 2 jars are available in Apigee Edge. The first two are
    produced by Apigee; contact Apigee support to obtain these jars to allow
    the compile, or get them here: 
    https://github.com/apigee/api-platform-samples/tree/master/doc-samples/java-cookbook/lib

    The Apache jars are also all available in Apigee Edge at runtime. To download them for compile time, you can get them from maven.org. 




