/*    _   _ _ _____ _    _              _____     _ _     ___ ___  _  __
 *   /_\ | | |_   _| |_ (_)_ _  __ _ __|_   _|_ _| | |__ / __|   \| |/ /
 *  / _ \| | | | | | ' \| | ' \/ _` (_-< | |/ _` | | / / \__ \ |) | ' <
 * /_/ \_\_|_| |_| |_||_|_|_||_\__, /__/ |_|\__,_|_|_\_\ |___/___/|_|\_\
 *                             |___/
 *
 * Copyright 2017 AllThingsTalk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.att.messaging;

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
import org.json.JSONObject;

import io.att.util.AttDevice;
import io.att.util.Device;


public class Mqtt implements MqttCallback {

    private MqttClient client;
    private MqttConnectOptions conOpt;
    private String brokerUrl;
    private String baseTopic;

    private AttDevice attdevice;

    private boolean isConnected = false;

    public Mqtt(Device device) {
        this.attdevice = device.getAttDevice();
        this.brokerUrl = Broker.getUrl();
        this.baseTopic = device.getPubTopic();
        String mqttClientId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the connection options object that contains connection parameters
            this.conOpt = new MqttConnectOptions();
            this.conOpt.setCleanSession(true);

            String code = "";
            String username = device.getToken();

            if (code != null) {
                conOpt.setPassword(code.toCharArray());
            }
            if (username != null) {
                conOpt.setUserName(username);
            }

            // Construct an MQTT blocking mode client
            client = new MqttClient(brokerUrl, mqttClientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // subscribe to asset state and commands for this device
        connect();
        try {
            subscribe(device.getSubTopic());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Mqtt initialized");
    }

    /****
     * Publish data to an asset on the device
     * @param asset
     * @param data
     */
    public void publish(String asset, String data) {
        if (data.startsWith("{") && data.endsWith("}"))
            data = "{\"Value\":" + data + "}";
        else
            data = "{\"Value\":\"" + data + "\"}";

        String topic = baseTopic + "asset/" + asset + "/state";

        System.out.println("Publish to: " + topic);
        System.out.println("Data      : " + data);
        publishAMessage(topic, data);
    }

    /****
     * Publish a binary payload to the device
     * @param data
     */
    public void publishBinary(String data) {
        String topic = baseTopic + "state";
        //data = "{\"Value\":\"" + data + "\"}";

        System.out.println("Publish binary payload to: " + topic);
        publishAMessage(topic, data);
    }

    private void publishAMessage(String topic, String pubMsg) {
        @SuppressWarnings("unused")
        MqttDeliveryToken token;
        MqttTopic mqttTopic = client.getTopic(topic);
        try {
            token = mqttTopic.publish(new MqttMessage(pubMsg.getBytes()));  // Publish to the broker
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to all state and command asset topics for this device.
     *
     * @param topicNames
     * @throws MqttException
     */
    public void subscribe(String[] topicNames) throws MqttException {
        for (String topic : topicNames) {
            System.out.println("Subscribing to: " + topic);
        }
        client.subscribe(topicNames);
    }

    public void subscribe(String topic) throws MqttException {
        System.out.println("Subscribing to: " + topic);
        client.subscribe(topic);
    }

    /**
     * Connect/disconnect
     */
    public void connect() {
        try {
            client.connect(conOpt);
            isConnected = true;
            System.out.println("Mqtt connected!");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }


    /**
     * Try reconnecting to the broker when connection is lost every second.
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection to " + brokerUrl + " lost!" + cause);
        isConnected = false;

        // try reconnecting
        while (!isConnected) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Trying to reconnect");
            this.connect();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
    }

    /**
     * Callback for incoming data over mqtt.
     * The value is sent back to the asset in AllThingsTalk to acknowledge we received it.
     *
     * @param topic
     * @param message
     * @throws InterruptedException
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws InterruptedException {
        String asset = topic.split("/")[3];
        String value = new String(message.getPayload());
        System.out.print("Actuator command received from asset: ");
        System.out.println(asset);

        Object val = new JSONObject(value).get("value");

        // publish value back to the cloud to acknowledge we received it
        publish(asset, val.toString());
        attdevice.callback(asset, val.toString());
    }
}