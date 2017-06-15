Start up your raspberry pi and log in through SSH (for example using PuTTy)

## Clone repo to Raspberry Pi

* git clone https://github.com/allthingstalk/java-sdk

## Add credentials to the example sketch

* cd java-sdk/src/examples/basic
* sudo nano BasicExample.java
* enter device id and token

```
  // device credentials
  private static final String deviceId  = "4scA0xAXs71iwbze3ugTAk7w";
  private static final String token     = "spicy:4OLvfJU4aMgVW1VeVsb6iOzJtOuyswvEghpcJQz0";
```

save and exit (ctrl-x; y)

## Run the example

* cd ~/java-sdk _(back to sdk root folder)_
* java -cp .:lib/*:src/examples io.att.examples.BasicExample