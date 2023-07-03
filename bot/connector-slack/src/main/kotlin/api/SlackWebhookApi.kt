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

package api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Path

/**
 * Use webhooks slack message : https://api.slack.com/messaging/webhooks
 */
interface SlackWebhookApi {
    fun sendMessage(
        @Path("outToken1") outToken1: String,
        @Path("outToken2") outToken2: String,
        @Path("outToken3") outToken3: String,
        @Body message: RequestBody
    ): Call<Void>
}
