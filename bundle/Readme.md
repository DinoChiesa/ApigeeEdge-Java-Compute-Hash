# Hash calculator and verifier sample proxy bundle

This directory contains the configuration for a sample proxy bundle
that shows how to use the Java custom policy for computing and verifying hashes. 

## Using the Proxy

Import and deploy the Proxy to your favorite Edge organization + environment.

To calculate a hash, invoke it like so:

```
curl -i -H 'content-type: text/plain' \
 -X POST \
 'http://ORGNAME-ENVNAME.apigee.net/hash-trial/calc?algorithm=sha256 \
 -d 'This is the next text Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas imperdiet auctor. Nullam sagittis felis eu sapien lobortis vitae faucibus orci pellentesque. Praesent elementum eros tellus, id bibendum justo. Sed posuere commodo enim vitae mollis.  Nullam sagittis felis eu sapien lobortis vitae faucibus orci pellentesque. Aliquam at mi eu arcu mattis imperdiet. Quisque lectus sapien, ornare in molestie sed, tristique id est. Quisque eu leo felis. Fusce pellentesque, turpis quis accumsan viverra, elit tellus imperdiet ligula, eu aliquam ante tortor eu massa. Integer hendrerit interdum lectus vitae lacinia. Integer id arcu a turpis mattis consectetur. Nullam ornare ligula et tellus tempus vehicula. Aliquam lobortis sem non nisi pellentesque mattis. Cras non est eu quam dictum tempor. Fusce rhoncus tempus nunc, at tincidunt lectus lacinia eget. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Ut ornare dui ac enim tempus consequat vitae sit amet orci. Quisque lectus sapien, ornare in molestie sed, tristique id est. Fusce rhoncus tempus nunc, at tincidunt lectus lacinia eget. Duis sem tortor, interdum congue viverra a, dictum ac risus.'
```

To verify a hash, invoke it like so:


## Bugs


