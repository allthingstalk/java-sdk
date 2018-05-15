Java SDK [![Build Status](https://travis-ci.org/allthingstalk/java-sdk.svg?branch=master)](https://travis-ci.org/allthingstalk/java-sdk)
---

### Installation

Get the `jar` from the Release page and include it in your project.

### Examples

Several basic examples are found in the `/src/examples` folder.
* `Counter.java` send data from your application to AllThingsTalk _(sensing)_
* `Actuation.java` toggle a boolean from AllThingsTalk _(actuation)_
* `BinaryPayload.java` send data of multiple assets together in one binary payload. The corresponding .json file for decoding the payload in AllThingsTalk can be found in the same folder.

### Device Credentials

You can either set them globally, using the same credentials for all applications using the sdk.
Or you can set them locally in a specific sketch, overriding the global settings.

Depending on how you initialize the device object in your application, the global or local credentials will be used.

* `device = new Device(this, "your_device_id", "your_device_token");` will use the provided local credentials
* `device = new Device(this);` will use the global credentials from the keys.json file

### Raspberry Pi support

#### Prerequisites

Java 1.8 installed on your Raspberry Pi.

[Raspian Jessie with Pixel](https://www.raspberrypi.org/downloads/raspbian/) has the latest Java version by default.

> Raspian Jessie lite does not come with Java preinstalled.

#### GPIO pin layout and setup

Please check [the pi4j documentation](http://pi4j.com/example/control.html).
Add the following _jars_ to your project
* `pi4j-core.jar`
* `pi4j-gpio-extension.jar`

You can download the _pi4j_ library [here](http://pi4j.com/download.html).

## Building

You can build the project locally using supplied `gradlew` wrapper:

```bash
gradlew jar
```

This will produce `.jar` in the `build/libs` directory.