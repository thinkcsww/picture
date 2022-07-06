package com.applory.pictureserverkt

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RestTemplateInterceptor(val token: String): ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val headers = request.headers
        if (!headers.containsKey("Authorization")) {
            headers.set("Authorization", "Bearer $token")
        }

        return execution.execute(request, body)
    }

}
