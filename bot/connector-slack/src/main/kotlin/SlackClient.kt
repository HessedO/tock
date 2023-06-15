/*
 * Copyright (C) 2017/2021 e-voyageurs technologies
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

package ai.tock.bot.connector.slack

import ai.tock.bot.connector.slack.model.SlackConnectorMessage
import ai.tock.shared.jackson.mapper
import ai.tock.shared.retrofitBuilderWithTimeoutAndLogger
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

object SlackClient {

    private val logger = KotlinLogging.logger { }

    interface SlackApi {
        @POST("/services/{outToken1}/{outToken2}/{outToken3}")
        fun sendMessage(
            @Path("outToken1") outToken1: String,
            @Path("outToken2") outToken2: String,
            @Path("outToken3") outToken3: String,
            @Body message: RequestBody
        ): Call<Void>
    }

    interface CustomSlackApi {
        @POST("/api/chat.postMessage")
        fun postMessage(@Header("Authorization") authorization: String, @Body message: RequestBody): Call<Void>
    }

    private val slackApi: SlackApi = retrofitBuilderWithTimeoutAndLogger(
        30000,
        logger
    )
        .baseUrl(SlackProperties.hooksBaseUrl)
        .build()
        .create(SlackApi::class.java)

    private val customSlackApi: CustomSlackApi = retrofitBuilderWithTimeoutAndLogger(
        30000,
        logger,
//        interceptors = listOf(
//            tokenAuthenticationInterceptor(retrieveTokenOauth())
//        ),
    )
        .baseUrl(SlackProperties.apiBaseUrl)
        .build()
        .create(CustomSlackApi::class.java)

    /**
     * Retrieve token Oauth
     * TODO : not finished
     */
    private fun retrieveTokenOauth(): String {
        // take a not null token between the two defined here
        return listOfNotNull(
            try {
                // TODO : finish token retrieve
//                if (clientId != null) {
//                    SlackOauthClient.slackOauth.authorize("chat:write", clientId).execute()
//                        .body()?.accessToken
                ""
            } catch (e: Exception) {
                throw Throwable("trouble retrieving slack token : ${e.message}")
            }
        ).first()

    }

    /**
     * Use webhooks slack message : https://api.slack.com/messaging/webhooks
     */
    fun sendMessage(outToken1: String, outToken2: String, outToken3: String, message: SlackConnectorMessage) {
        val body = RequestBody.create("application/json".toMediaType(), mapper.writeValueAsBytes(message))
        val response = slackApi.sendMessage(outToken1, outToken2, outToken3, body).execute()
        logger.debug { response }
    }

    /**
     * Use https://api.slack.com/methods/chat.postMessage
     */
    fun postMessage(authorization: String, message: SlackConnectorMessage) {
        val body =
            RequestBody.create("application/json; charset=utf-8".toMediaType(), mapper.writeValueAsBytes(message))
        val response = customSlackApi.postMessage(authorization, body).execute()
        logger.debug { response }
    }

}