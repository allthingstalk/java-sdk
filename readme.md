## Prerequisites

Java 1.8 installed on your Raspberry Pi.

[Raspian Jessie with Pixel](https://www.raspberrypi.org/downloads/raspbian/) has the latest Java version by default.

_Note:_ Raspian Jessie lite does not come with Java preinstalled.

## GPIO pin layout and setup

* Please check [the pi4j documentation](http://pi4j.com/example/control.html)
* Connect LED as shown in the above image

## Example

This basic example consist of a **LED** _(actuator)_ and **counter** _(sensor)_
Start up your raspberry pi and log in through SSH (for example using PuTTy)

### Clone repo to Raspberry Pi

* `git clone https://github.com/allthingstalk/java-sdk`

### Create your device on AllThingsTalk

* Create an account on AllthingsTalk
* Create a device. You will need the _deviceId_ and _token_ later on for authentication

### Add credentials to the example sketch

* `cd java-sdk/src/examples`
* `sudo nano BasicExample.java`
* enter device id and token _(required)_ and endpoint _(optional)_

```
  // device credentials
  private static final String deviceId  = "4scA0xAXs71iwbze3ugTAk7w";
  private static final String token     = "spicy:4OLvfJU4aMgVW1VeVsb6iOzJtOuyswvEghpcJQz0";
  private static final String endpoint  = "";  // optional
```

* save and exit (ctrl-x; y)

> If no endpoint is provided, the default _api.allthingstalk.io_ will be used for http and mqtt

### Compile the example

* `cd ~/java-sdk` _(back to sdk root folder)_
* `javac -cp .:lib/* src/examples/BasicExample.java` 

### Run the example

* `sudo java -cp .:lib/*:src/examples BasicExample`
_(sudo access needed to access the GPIO pins)_
