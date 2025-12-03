package com.zunza.ecommerce.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws")
data class S3Properties(
    var auth: String = "",
    var s3: S3 = S3()
) {
    data class S3(
        var bucket: String = "",
        var region: String = "",
        var profile: String = ""
    )
}
