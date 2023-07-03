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
import ai.tock.shared.tokenAuthenticationInterceptor
import api.SlackCurrentApi
import api.SlackWebhookApi
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

class SlackClient(val oauthToken: String?) {

    private val logger = KotlinLogging.logger { }

    private val slackApi: SlackWebhookApi = retrofitBuilderWithTimeoutAndLogger(
        30000,
        logger
    )
        .baseUrl(SlackProperties.hooksBaseUrl)
        .build()
        .create(SlackWebhookApi::class.java)

    private val customSlackApi: SlackCurrentApi = retrofitBuilderWithTimeoutAndLogger(
        30000,
        logger,
        interceptors = listOf(
            tokenAuthenticationInterceptor(retrieveTokenOauth())
        ),
    )
        .baseUrl(SlackProperties.apiBaseUrl)
        .build()
        .create(SlackCurrentApi::class.java)

    /**
     * Retrieve token Oauth
     */
    private fun retrieveTokenOauth(): String {
        // take a not null token between the two defined here
        var token: String? = null
            try {
                token = oauthToken ?: SlackProperties.oauthToken ?: kotlin.run {
                    logger.debug { "no oauthToken for slack api check configuration in studio or property tock_slack_oauth_token" }
                    throw Throwable("check check connector configuration in studio $oauthToken configuration or property tock_slack_oauth_token") }
            } catch (e: Exception) {
                throw Throwable("trouble retrieving slack token : ${e.message}")
            }
        return token
    }

    private fun generateOauthTokenFromSlackApi(){
        NotImplementedError("not implemented yet")
        //                if (clientId != null) {
        //                    SlackOauthClient.slackOauth.authorize("chat:write", clientId).execute()
        //                        .body()?.accessToken
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