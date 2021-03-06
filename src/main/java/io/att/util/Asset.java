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

package io.att.util;

public enum Asset {

    INTEGER("\"integer\""),
    NUMBER("\"number\""),
    STRING("\"string\""),
    BOOLEAN("\"boolean\""),
    LOCATION("\"object\",\"properties\":{\"latitude\":{\"type\":\"number\"}, \"longitude\":{\"type\":\"number\"}, \"altitude\":{\"type\":\"number\"}}}"),
    POSITION("\"object\",\"properties\":{\"x\":{\"type\":\"number\"}, \"y\":{\"type\":\"number\"}, \"z\":{\"type\":\"number\"}}}"),
    COLOR("\"object\",\"properties\":{\"r\":{\"type\":\"integer\",\"minimum\":0,\"maximum\":255},\"g\":{\"type\":\"integer\",\"minimum\":0,\"maximum\":255},\"b\":{\"type\":\"integer\",\"minimum\":0,\"maximum\":255}}}");

    private String profile;

    Asset(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }
}