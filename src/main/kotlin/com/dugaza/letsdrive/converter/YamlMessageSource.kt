package com.dugaza.letsdrive.converter

import com.dugaza.letsdrive.logger
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.AbstractMessageSource
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import java.text.MessageFormat
import java.util.Locale

class YamlMessageSource : AbstractMessageSource() {
    private val log = logger()
    private var messages: Map<String, Any> = emptyMap()

    init {
        log.error("messages_${LocaleContextHolder.getLocale()}.yml")
        val resourceName = "messages_${LocaleContextHolder.getLocale()}.yml"
        val resource = ClassPathResource("messages/$resourceName")
        messages = Yaml().load(resource.inputStream)
    }

    override fun resolveCode(
        code: String,
        locale: Locale,
    ): MessageFormat? {
        val message = getMessageFromYaml(code, messages)
        return message?.let { MessageFormat(it, locale) }
    }

    private fun getMessageFromYaml(
        code: String,
        messages: Map<String, Any>,
    ): String? {
        val keys = code.split(".")
        var value: Any? = messages
        for (key in keys) {
            if (value is Map<*, *>) {
                value =
                    value.entries.find { (entryKey, _) ->
                        entryKey.toString() == key || entryKey.toString() == key.toIntOrNull()?.toString()
                    }?.value
            } else {
                return null
            }
        }
        return value as? String
    }
}
