/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.common.config;

import org.apache.seatunnel.shade.com.typesafe.config.Config;

import static org.apache.seatunnel.common.Constants.CHECK_SUCCESS;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckConfigUtil {

    public static CheckResult check(Config config, String... params) {
        StringBuilder missingParams = new StringBuilder();
        for (String param : params) {
            if (!config.hasPath(param) || config.getAnyRef(param) == null) {
                missingParams.append(param).append(",");
            }
        }

        if (missingParams.length() > 0) {
            String errorMsg = String.format("please specify [%s] as non-empty",
                    missingParams.deleteCharAt(missingParams.length() - 1));
            return new CheckResult(false, errorMsg);
        } else {
            return new CheckResult(true, CHECK_SUCCESS);
        }
    }

    /**
     * check config if there was at least one usable
     */
    public static CheckResult checkOne(Config config, String... params) {
        if (params.length == 0) {
            return new CheckResult(true, "");
        }

        List<String> missingParams = new LinkedList();
        for (String param : params) {
            if (!config.hasPath(param) || config.getAnyRef(param) == null) {
                missingParams.add(param);
            }
        }

        if (missingParams.size() == params.length) {
            String errorMsg = String.format("please specify at least one config of [%s] as non-empty",
                    missingParams.stream().collect(Collectors.joining(",")));
            return new CheckResult(false, errorMsg);
        } else {
            return new CheckResult(true, CHECK_SUCCESS);
        }
    }

    /**
     * merge all check result
     */
    public static CheckResult mergeCheckMessage(CheckResult... checkResults) {
        List<CheckResult> notPassConfig = Arrays.stream(checkResults).filter(item -> !item.isSuccess()).collect(Collectors.toList());
        if (notPassConfig.isEmpty()) {
            return new CheckResult(true, CHECK_SUCCESS);
        } else {
            String errMessage = notPassConfig.stream().map(CheckResult::getMsg).collect(Collectors.joining(","));
            return new CheckResult(false, errMessage);
        }

    }
}
