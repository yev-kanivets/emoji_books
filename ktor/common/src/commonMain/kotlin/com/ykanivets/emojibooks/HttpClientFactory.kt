package com.ykanivets.emojibooks

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

const val BACKEND_LINK = "boiling-brushlands-48368.herokuapp.com"

class HttpClientFactory {

    fun create(
        token: String
    ) = HttpClient {
        defaultRequest {
            url {
                host = BACKEND_LINK
                protocol = URLProtocol.HTTPS
            }
            header("Authorization", "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        Json {
            serializer = KotlinxSerializer()
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
