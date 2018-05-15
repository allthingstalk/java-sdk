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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class PayloadBuilder {

    private static final short INT_LENGTH = 4;
    private static final short FLOAT_LENGTH = 4;
    private static final short BOOLEAN_LENGTH = 1;

    private int length;
    private Mqtt mqtt;
    private List<Byte> payload;

    public PayloadBuilder(Mqtt mqtt) {
        this.mqtt = mqtt;
        reset();
    }

    /****
     * Reset the lenght of the payload
     */
    public void reset() {
        length = 0;
        payload = new ArrayList<Byte>();
    }

    /****
     * Get the length of the current payload
     * @return
     */
    public int getLength() {
        return length;
    }

    /****
     * Publish a binary payload to the device
     *
     * @param value
     */
    public void send() {
        byte[] bytes = new byte[payload.size()];
        for (int i = 0; i < payload.size(); i++) {
            bytes[i] = payload.get(i);  // Copy list to array
        }

        mqtt.publishBinary(new String(bytes));
    }

    /****
     * Add a single byte to our binary payload
     *
     * @param b
     * @return
     */
    public int addByte(String b) {
        try {
            byte bt = (byte) (Integer.parseInt(b, 16) & 0xff);  // Parse byte from string ("00" to "FF");
            payload.add(bt);
            length += 1;
            return 1;
        } catch (Exception pe) {
            System.out.println("Could not parse byte value");
        }
        return -1;
    }

    public void addBytes(String bytes) {
        bytes = bytes.replace(" ", "");  // Get rid of spaces
        for (int i = 0; i < bytes.length(); i += 2) {
            String str = bytes.substring(i, i + 2);
            addByte(str);
        }
    }

    /****
     * Add a boolean to our binary payload
     *
     * @param b
     * @return
     */
    public int addBoolean(boolean b) {
        byte bite = b == true ? (byte) 1 : (byte) 0;
        payload.add(bite);

        System.out.println("Added " + BOOLEAN_LENGTH + " bytes to the payload");
        length += BOOLEAN_LENGTH;
        return BOOLEAN_LENGTH;
    }

    /****
     * Add an integer to our binary payload
     *
     * @param i
     * @return
     */
    public int addInteger(int i) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
        for (byte b : bytes) {
            payload.add(b);
        }

        System.out.println("Added " + INT_LENGTH + " bytes to the payload");
        length += INT_LENGTH;
        return INT_LENGTH;
    }

    /****
     * Add a float to our binary payload
     *
     * @param f
     * @return
     */
    public int addNumber(float f) {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array();
        for (byte b : bytes) {
            payload.add(b);
        }

        System.out.println("Added " + FLOAT_LENGTH + " bytes to the payload");
        length += FLOAT_LENGTH;
        return FLOAT_LENGTH;
    }

    /****
     * Add a string to our binary payload
     *
     * @param s
     * @return
     */
    public int addString(String s) {
        byte[] bytes = s.getBytes();
        for (byte b : bytes) {
            payload.add(b);
        }

        int l = bytes.length;
        System.out.println("Added " + l + " bytes to the payload");
        length += l;
        return l;
    }
}