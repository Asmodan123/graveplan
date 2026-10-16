package de.hannisoft.graveplan.properties

import java.util.*

data class WebsiteConfig(
    val authUserFile: String,
    val internalUsers: List<String>,
    val publicUsers: List<String>,
    val rootRedirect: String
)

/**
 * Reads website.properties (part of the repo) and overrides its values with
 * website.local.properties (not part of the repo, contains user names and server path).
 */
class WebsiteConfigReader {
    fun read(): WebsiteConfig {
        val props = Properties()
        load(props, "website.properties")
        load(props, "website.local.properties")

        fun value(key: String): String =
            props.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
                ?: throw IllegalArgumentException("Missing property '$key' in website.properties / website.local.properties")

        fun users(key: String): List<String> =
            value(key).split(",").map { it.trim() }.filter { it.isNotEmpty() }

        return WebsiteConfig(
            authUserFile = value("authUserFile"),
            internalUsers = users("users.intern"),
            publicUsers = users("users.public"),
            rootRedirect = value("rootRedirect")
        )
    }

    private fun load(props: Properties, resource: String) {
        javaClass.classLoader.getResourceAsStream(resource)
            ?.reader(Charsets.UTF_8)
            ?.use { props.load(it) }
            ?: println("Website config $resource not found")
    }
}
