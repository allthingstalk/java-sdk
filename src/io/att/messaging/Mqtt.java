package io.att.messaging;

import io.att.util.AttDevice;
import io.att.util.Device;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Mqtt implements MqttCallback {
  
  @SuppressWarnings("unused")
  private static int qos = 0; // 0, 1 or 2 quality of service
  
  private MqttClient         client;
  private MqttConnectOptions conOpt;
  private String             brokerUrl;
  private String             mqttClientId;
  private String             baseTopic;
  
  private AttDevice attdevice;
  
  private boolean isConnected = false;
  
  public Mqtt(Device device)
  {
    this.attdevice    = device.getAttDevice();
    this.brokerUrl    = Broker.getUrl();
    this.baseTopic    = device.getPubTopic();
    this.mqttClientId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    
    String tmpDir = System.getProperty("java.io.tmpdir");
    MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
    
    try
    {      
      // Construct the connection options object that contains connection parameters
      this.conOpt = new MqttConnectOptions();
      this.conOpt.setCleanSession(true);
      
      //String code     = device.getClientKey();
      //String username = device.getClientId() + ":" + device.getClientId();
      String code = "";
      String username = device.getToken();

      if(code     != null) { conOpt.setPassword(code.toCharArray()); }
      if(username != null) { conOpt.setUserName(username); }

      // Construct an MQTT blocking mode client
      client = new MqttClient(brokerUrl, mqttClientId, dataStore);

      // Set this wrapper as the callback handler
      client.setCallback(this);
    }
    catch (MqttException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    
    // subscribe to asset state and commands for this device
    connect();
    /*
    try
    {
      subscribe(device.getSubTopic());
    } catch (MqttException e) {
      e.printStackTrace();
    }
    */
    System.out.println("Mqtt initialized");
  }
  
  /****
   * publish asset to topic
   * @param data
   */
  public void publish(String asset, String data)
  {
    String topic = baseTopic + asset + "/state";
    data = "{\"Value\":\"" + data + "\"}";
    
    System.out.println("Publish to: " + topic);
    System.out.println("Data      : " + data);
    publishAMessage(topic, data);
  }
  
  private void publishAMessage(String topic, String pubMsg)
  {    
    @SuppressWarnings("unused")
    MqttDeliveryToken token;
    MqttTopic mqttTopic = client.getTopic(topic);
    try
    {
      token = mqttTopic.publish(new MqttMessage(pubMsg.getBytes()));  // Publish to the broker
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  /****
   * subscribe to all state and command asset topics for this device
   * @param topicNames
   * @throws MqttException
   */
  public void subscribe(String[] topicNames) throws MqttException
  {
    for(String topic : topicNames)
    {
      System.out.println("Subscribing to: " + topic);
    }
    client.subscribe(topicNames);
  }
  
  public void subscribe(String topic) throws MqttException
  {
    System.out.println("Subscribing to: " + topic);
    client.subscribe(topic);
  }

  /****
   * connect/disconnect
   */  
  public void connect()
  {
    try
    {
      client.connect(conOpt);
      isConnected = true;
      System.out.println("Mqtt connected!");
    }
    catch (MqttException e)
    {
      e.printStackTrace();
    }
  }
  
  public void disconnect() throws MqttException
  {
    client.disconnect();
    isConnected = false;
  }
  
  public boolean isConnected()
  {
    return isConnected;
  }


  @Override
  /****
   * try reconnecting to the broker when connection is lost every second
   */
  public void connectionLost(Throwable cause)
  {
    System.out.println("Connection to " + brokerUrl + " lost!" + cause);
    isConnected = false;
    
    // try reconnecting
    while(!isConnected)
    {
      try
      {
        Thread.sleep(1000);
      }
      catch(InterruptedException e){
        e.printStackTrace();
      }
      
      System.out.println("Trying to reconnect");
      this.connect();
    }
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken arg0)
  {
  }
  
  @Override
  /****
   * determine whether incoming message is a config update or datapoint
   */
  public void messageArrived(String topic, MqttMessage message) throws InterruptedException
  {
    String asset = topic.split("/")[6];
    String value = new String(message.getPayload());
    System.out.println("Actuator command received:");
    System.out.println(asset);
    System.out.println(value);
    
    // publish value back to the cloud to acknowledge we received it
    publish(asset, value);
    attdevice.callback(asset, value);
  }
}