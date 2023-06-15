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

package ai.tock.bot.connector.slack.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

/**
 * Event API message base class.
 */
@JsonTypeInfo(
    use = NAME,
    include = PROPERTY,
    property = "type"
)
@JsonSubTypes(
    value = [
        Type(AppHomeOpenedEvent::class, name = "app_home_opened"),
        Type(MessageEvent::class, name = "message")
    ]
)

/**
 * The params below are available in each types
 * @param channel
 * @param user
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class EventSubTypeApiMessage(open val channel: String, open val user: String) :
    SlackConnectorMessage()

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppHomeOpenedEvent(
    override val user: String,
    override val channel: String,
    val tab: String,
    @JsonProperty("event_ts")
    val eventTs: String,
) : EventSubTypeApiMessage(channel, user)

/**
 * A [Message event](https://api.slack.com/events/message).
 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEvent(
    /** to know if the bot answered or not**/
    val ok : Boolean?,
    @JsonProperty("client_msg_id")
    val clientMsgIds: String?,
    /** message with botId means th message is from the bot**/
    @JsonProperty("bot_id")
    val botId : String?,
    override val channel: String,
    override val user: String,
    val ts: String,
    val subtype: String?,
    val text: String,
) : EventSubTypeApiMessage(channel, user)