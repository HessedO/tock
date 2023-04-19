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

package model.oauth

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

//inspired from https://api.slack.com/authentication/oauth-v2#exchanging
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OauthAccess(
    val ok: Boolean,
    val tokenType: String,
    val accessToken: String,
    val scope: String,
    val botUserId: String,
    val appId: String,
    val team: Team,
    val enterprise: Enterprise,
    val authedUser: AuthedUser
)

data class Team(
    val name: String,
    val id: String
)

data class Enterprise(
    val name: String,
    val id: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AuthedUser(
    val id: String,
    val scope: String,
    val accessToken: String,
    val tokenType: String
)