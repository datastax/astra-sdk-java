package com.dtsx.astra.sdk.utils.observability;

/*-
 * #%L
 * Data API Java Client
 * --
 * Copyright (C) 2024 DataStax
 * --
 * Licensed under the Apache License, Version 2.0
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Defines the contract for observers that react to command executions within the DataApiClient.
 * Implementing this interface allows for the execution of synchronous treatments in response to command execution events.
 * These treatments can include logging, metrics collection, or any other form of monitoring or post-execution processing.
 * <p>
 * By registering a {@code CommandObserver} with a {@code DataApiClient}, clients can extend the client's functionality
 * in a decoupled manner, enabling custom behaviors such as logging command details, pushing metrics to a monitoring system,
 * or even triggering additional business logic based on the command's execution.
 * </p>
 * <p>
 * This interface is particularly useful in scenarios where actions need to be taken immediately after a command's execution,
 * and where those actions might depend on the outcome or metadata of the command. Implementers can receive detailed
 * information about the command's execution through the {@link ApiExecutionInfos} parameter, allowing for rich and context-aware processing.
 * </p>
 * Example use cases include:
 * <ul>
 *     <li>Logging command execution details for audit or debugging purposes.</li>
 *     <li>Collecting performance metrics of command executions to monitor the health and performance of the application.</li>
 *     <li>Triggering additional processes or workflows based on the success or failure of a command.</li>
 * </ul>
 */
public interface ApiRequestObserver {

    /**
     * Invoked when a command is executed, providing an opportunity for registered observers to perform
     * synchronous post-execution treatments based on the command's execution information.
     * <p>
     * Implementers should define the logic within this method to handle the command execution event, utilizing
     * the {@link ApiExecutionInfos} provided to access details about the command's execution context, results, and status.
     * This method is called synchronously, ensuring that any processing here is completed before the command execution
     * flow continues.
     * </p>
     *
     * @param executionInfo The {@link ApiExecutionInfos} containing detailed information about the executed command,
     * including execution context, results, and any errors or warnings that occurred.
     */
    void onRequest(ApiExecutionInfos executionInfo);
}
