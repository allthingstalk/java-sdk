## GPIO pin layout and setup

* Please check [the pi4j documentation](http://pi4j.com/example/control.html)
* Connect LED as shown in the above image

## Example

Start up your raspberry pi and log in through SSH (for example using PuTTy)

### Clone repo to Raspberry Pi

* `git clone https://github.com/allthingstalk/java-sdk`

### Add credentials to the example sketch

* `cd java-sdk/src/examples`
* `sudo nano BasicExample.java`
* enter device id and token

```
  // device credentials
  private static final String deviceId  = "4scA0xAXs71iwbze3ugTAk7w";
  private static final String token     = "spicy:4OLvfJU4aMgVW1VeVsb6iOzJtOuyswvEghpcJQz0";
```

* save and exit (ctrl-x; y)

### Compile the example

* `cd ~/java-sdk _(back to sdk root folder)_`
* `javac -cp .:lib/* src/examples/BasicExample.java` 

### Run the example

* `sudo java -cp .:lib/*:src/examples BasicExample`
_(sudo access needed to access the GPIO pins)_