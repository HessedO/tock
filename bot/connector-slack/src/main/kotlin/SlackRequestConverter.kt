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

import ai.tock.bot.connector.slack.model.AppHomeOpenedEvent
import ai.tock.bot.connector.slack.model.CallbackEvent
import ai.tock.bot.connector.slack.model.EventApiMessage
import ai.tock.bot.connector.slack.model.InteractiveMessageEvent
import ai.tock.bot.connector.slack.model.MessageEvent
import ai.tock.bot.connector.slack.model.old.SlackMessageIn
import ai.tock.bot.engine.action.SendChoice
import ai.tock.bot.engine.action.SendSentence
import ai.tock.bot.engine.event.Event
import ai.tock.bot.engine.event.StartConversationEvent
import ai.tock.bot.engine.user.PlayerId
import ai.tock.bot.engine.user.PlayerType.bot
import ai.tock.bot.engine.user.PlayerType.user
import mu.KotlinLogging

internal object SlackRequestConverter {

    private val logger = KotlinLogging.logger {}

    fun toEvent(event: EventApiMessage, applicationId: String): Event? =
        if (event is InteractiveMessageEvent) {
            val params = SendChoice.decodeChoiceId(event.actions.first().value)
            SendChoice(
                PlayerId(event.user.id),
                applicationId,
                PlayerId(applicationId, bot),
                params.first,
                params.second
            )
        } else if (event is CallbackEvent) {
            event.event.let { subMessage ->
                if (subMessage.user == null) null
                else if(subMessage is AppHomeOpenedEvent){
                    logger.debug { "app opened from ${subMessage.user}" }
                    null
                }
                else {
                    if(subMessage is MessageEvent && subMessage.botId == null) {
                        //trigger the sentence sended only from the user
                        SendSentence(
                            PlayerId(subMessage.user),
                            applicationId,
                            PlayerId(applicationId, bot),
                            subMessage.text,
                        )
                    } else {
                        null
                    }
                }

            }
        } else {
            logger.warn { "unhandled event: $event" }
            null
        }

    fun toEvent(message: SlackMessageIn, applicationId: String): Event? {
        val safeMessage = message
        safeMessage.text = message.getRealMessage()
        return SendSentence(
            PlayerId(message.user_id),
            applicationId,
            PlayerId("", bot),
            safeMessage.text,
            mutableListOf(safeMessage)
        )
    }
}
