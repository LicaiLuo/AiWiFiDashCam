package cn.anc.dashcam.core.logging

object LogSanitizer {

    private val keyValuePatterns = listOf(
        Regex("(?i)(password|pwd|token|access_token|refresh_token|authorization)\\s*[:=]\\s*[^\\s,&]+"),
        Regex("(?i)(wifiPassword|wifi_password)\\s*[:=]\\s*[^\\s,&]+"),
    )

    private val phonePattern = Regex("(?<!\\d)(1[3-9]\\d)\\d{4}(\\d{4})(?!\\d)")
    private val emailPattern = Regex("([A-Za-z0-9._%+-])[A-Za-z0-9._%+-]*(@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})")
    private val queryPattern = Regex("([?&])([^=\\s&]+)=([^&\\s]+)")

    fun sanitize(value: String): String {
        var sanitized = value

        keyValuePatterns.forEach { pattern ->
            sanitized = pattern.replace(sanitized) { match ->
                val key = match.groupValues[1]
                "$key=***"
            }
        }

        sanitized = phonePattern.replace(sanitized, "$1****$2")
        sanitized = emailPattern.replace(sanitized, "$1***$2")
        sanitized = queryPattern.replace(sanitized) { match ->
            "${match.groupValues[1]}${match.groupValues[2]}=***"
        }

        return sanitized
    }
}
