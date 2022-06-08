package com.applory.pictureserverkt.oauth

import com.applory.pictureserverkt.config.AppConfiguration
import com.applory.pictureserverkt.exception.NotFoundException
import com.applory.pictureserverkt.exception.UnauthorizedException
import com.applory.pictureserverkt.user.UserRepository
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val restTemplate: RestTemplate,
    private val appConfiguration: AppConfiguration,
    private val objectMapper: ObjectMapper,
    private val environment: Environment
) {
    val KAKAO_VALIDATE_TOKEN_URL = "https://kapi.kakao.com/v1/user/access_token_info"

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    fun login(dto: AuthDto.Login, baseUrl: String): Oauth2Token {
        val userInDB = userRepository.findByUsername(dto.username)

        if (userInDB == null) {
            throw NotFoundException(dto.username + " not found")
        }

        checkKakaoToken(dto.kakaoToken, baseUrl)

        val oauth2Token: Oauth2Token = getToken(dto, baseUrl)

        return oauth2Token

    }

    private fun checkKakaoToken(kakaoToken: String, baseUrl: String) {
        if (environment.acceptsProfiles(Profiles.of("test")) && "test".equals(kakaoToken)) {
            return
        }

        val headers = HttpHeaders()
        headers["Content-Type"] = "application/x-www-form-urlencoded;charset=utf-8"
        headers["Authorization"] = "Bearer $kakaoToken"

        val httpEntity = HttpEntity<Any>(headers)

        try {
            restTemplate.exchange(KAKAO_VALIDATE_TOKEN_URL, HttpMethod.GET, httpEntity, HashMap::class.java)
        } catch (e: HttpClientErrorException) {
            throw UnauthorizedException(e.message)
        }
    }

    private fun getToken(dto: AuthDto.Login, baseUrl: String): Oauth2Token {
        val crendentials: String = "${appConfiguration.clientId}:${appConfiguration.clientSecret}"
        val encodedCredentials: String = String(Base64.getEncoder().encode(crendentials.encodeToByteArray()))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Authorization", "Basic $encodedCredentials")

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "password")
        params.add("username", dto.username)
        params.add("password", dto.username + appConfiguration.pwSalt)
        params.add("scope", "read write")

        val httpEntity = HttpEntity(params, headers)

        val response = restTemplate.postForEntity("$baseUrl/oauth/token", httpEntity, String::class.java)
        if (response.statusCode == HttpStatus.OK) {
            return try {
                objectMapper.readValue(response.body, Oauth2Token::class.java)
            } catch (e: JsonProcessingException) {
                throw UnauthorizedException("Invalid Oauth token request")
            }
        }

        throw UnauthorizedException("Invalid Oauth token request")

    }

    fun refreshToken(dto: AuthDto.RefreshToken, baseUrl: String): Oauth2Token {
        val crendentials: String = "${appConfiguration.clientId}:${appConfiguration.clientSecret}"
        val encodedCredentials: String = String(Base64.getEncoder().encode(crendentials.encodeToByteArray()))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Authorization", "Basic $encodedCredentials")

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "refresh_token")
        params.add("refresh_token", dto.refreshToken)

        val httpEntity = HttpEntity(params, headers)

        try {
            val response = restTemplate.postForEntity("$baseUrl/oauth/token", httpEntity, String::class.java)

            if (response.statusCode == HttpStatus.OK) {
                return objectMapper.readValue(response.body, Oauth2Token::class.java)
            }
        } catch (e: Exception) {
            log.error("refreshToken error: " + e.message)
            throw UnauthorizedException("Invalid Refresh Token")
        }

        throw UnauthorizedException("Invalid Refresh Token")
    }
}
