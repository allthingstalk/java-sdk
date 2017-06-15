import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
//import com.pi4j.wiringpi.Gpio;

import io.att.util.AttDevice;
import io.att.util.Device;

public class BasicExample implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "4scA0xAXs71iwbze3ugTAk7w";
  private static final String token     = "spicy:4OLvfJU4aMgVW1VeVsb6iOzJtOuyswvEghpcJQz0";
  
  static Device device;
  static final GpioController gpio = GpioFactory.getInstance();
  
  // sensor pin setup
  GpioPinDigitalOutput ledPin;
  GpioPinAnalogInput   buttonPin; 
 
  // variables
  static boolean ledValue;
  static int count = 0;

  public void setup()
  {
    // Initialize the device
    device = new Device(deviceId, token, this);

    // Set up Http and create assets
    device.setupHttp();
    device.getHttp().addAsset("Counter", "Counter", "Tick counter", "sensor", "integer");
    device.getHttp().addAsset("LED", "Blinky", "Blinking LED", "actuator", "boolean");
    
    // Set (initial) asset states through http
    device.setAssetState("Counter", 10);
    device.setAssetState("LED", false);
    
    // Initialize gpio pins
    ledPin    = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    //buttonPin = gpio.provisionAnalogInputPin(RaspiPin.GPIO_02);
    //Gpio.pinMode(2, Gpio.INPUT);
    
    try{Thread.sleep(1000);}catch(Exception e){}
    
    // Subscribe to mqtt
    device.setupMqtt();
    device.setRate(1);  // set loop at x ticks per second (tick method is called at this rate)
    
    // Initializing done. Start looping
    device.start();
  }

  public void tick()
  {
    count++;
    device.publish("Counter", count);
    
    //int button = Gpio.analogRead(2);
  }
  
  public void callback(String asset, String value)
  {
    if(asset.equals("LED"))
      ledValue = Boolean.parseBoolean(value);
    
    if(ledValue == true)
      ledPin.high();  // turn on the LED
    else
      ledPin.low();  // turn off the LED
    
    System.out.println(ledValue);
  }
  
  //
  public static void main(String[] args)
  {
    new BasicExample().setup();
  }  
}