/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ayfaar.ii.spring.handler;

import org.springframework.util.ObjectUtils;

public class BusinessError {

    private final String code;
    private String message;
    private String debug;

    public BusinessError(String code, String message, String debug) {
        this.code = code;
        this.message = message;
        this.debug = debug;
    }

    public BusinessError(String message, String debug) {
        this.code = "UNDEFINED";
        this.message = message;
        this.debug = debug != null && (debug.equals(message) || message.contains(debug)) ? null : debug;
    }

    public BusinessError(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getDebug() {
        return debug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BusinessError) {
            BusinessError re = (BusinessError) o;
            return  ObjectUtils.nullSafeEquals(getCode(), re.getCode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(new Object[]{
                getMessage(), getDebug()
        });
    }

    public String toString() {
        return getMessage();
    }

    public String getCode() {
        return code;
    }
}