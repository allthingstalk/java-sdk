Java SDK
---

### Installation

Download the repository and add the `ATT_IoT_sdk.jar` file to your projects build path.

### Examples

Two basic examples are found in the `/examples` folder.
* `counter.ino` send data from your application to AllThingsTalk _(sensing)_
* `actuation.ino` toggle a boolean from AllThingsTalk _(actuation)_

---

For more information, please check out our [documentation](http://docs.allthingstalk.com/developers/sdk/java)

---

### Raspberry Pi support

#### Prerequisites

Java 1.8 installed on your Raspberry Pi.

[Raspian Jessie with Pixel](https://www.raspberrypi.org/downloads/raspbian/) has the latest Java version by default.

_Note:_ Raspian Jessie lite does not come with Java preinstalled.

#### GPIO pin layout and setup

Please check [the pi4j documentation](http://pi4j.com/example/control.html)