package com.applory.pictureserverkt

import com.applory.pictureserverkt.config.AppConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration::class)
class PictureServerKtApplication

fun main(args: Array<String>) {
    runApplication<PictureServerKtApplication>(*args)
}
