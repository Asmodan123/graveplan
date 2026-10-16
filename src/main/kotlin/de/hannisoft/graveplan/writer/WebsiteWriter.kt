package de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.properties.WebsiteConfig
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul
import java.io.File

const val PLAN_DIR = "plan"
const val INTERNAL_DIR = "internal"
const val PUBLIC_DIR = "public"

/**
 * Writes the static frame of the website (root redirect, landing page, access control).
 * The content directories plan/internal/<ts>/ and plan/public/<ts>/ are written by GravePlanWriter.
 */
class WebsiteWriter(private val websiteDir: String, private val config: WebsiteConfig) {

    private val authName = "Friedhofsplan Pattensen"

    fun write(timeStampString: String, dueDate: String) {
        val planDir = File(websiteDir, PLAN_DIR)
        writeRootHtaccess()
        writeFile(File(websiteDir, "robots.txt"), "User-agent: *\nDisallow: /$PLAN_DIR/\n")
        writePlanHtaccess(planDir, timeStampString)
        writeLandingPage(planDir, timeStampString, dueDate)
        writeFile(File(planDir, "$INTERNAL_DIR/.htaccess"), authConfig(config.internalUsers))
        writeFile(File(planDir, "$PUBLIC_DIR/.htaccess"), authConfig(config.internalUsers + config.publicUsers))
        println("Created website frame in $websiteDir for due date $dueDate")
    }

    private fun writeRootHtaccess() {
        writeFile(File(websiteDir, ".htaccess"), """
            |RewriteEngine On
            |$HTTPS_REDIRECT
            |
            |# Startseite leitet auf die Friedhofsseite der Kirchengemeinde weiter
            |RewriteRule ^$ ${config.rootRedirect} [R=302,L]
            |""".trimMargin())
    }

    private fun writePlanHtaccess(planDir: File, timeStampString: String) {
        val internalUsersRegex = config.internalUsers.joinToString("|") { escapeRegex(it) }
        writeFile(File(planDir, ".htaccess"), """
            |${authConfig(config.internalUsers + config.publicUsers)}
            |DirectoryIndex index.html
            |AddDefaultCharset UTF-8
            |AddCharset UTF-8 .css .js
            |
            |<IfModule mod_headers.c>
            |    <FilesMatch "\.html$">
            |        Header set Cache-Control "no-cache"
            |    </FilesMatch>
            |</IfModule>
            |
            |RewriteEngine On
            |RewriteBase /$PLAN_DIR/
            |$HTTPS_REDIRECT
            |
            |# Nach dem Login je nach Benutzer auf den aktuellen Stichtag der internen bzw. öffentlichen Version weiterleiten
            |RewriteCond %{REMOTE_USER} ^($internalUsersRegex)$
            |RewriteRule ^(index\.html)?$ $INTERNAL_DIR/$timeStampString/Friedhofsplan.html [R=302,L]
            |RewriteRule ^(index\.html)?$ $PUBLIC_DIR/$timeStampString/Friedhofsplan.html [R=302,L]
            |""".trimMargin())
    }

    /** Fallback if the redirect in plan/.htaccess does not apply. */
    private fun writeLandingPage(planDir: File, timeStampString: String, dueDate: String) {
        val html = createHTML().html {
            head {
                meta(charset = "utf-8")
                link(rel = "icon", href = "$PUBLIC_DIR/$timeStampString/plan/plan.ico", type = "image/x-icon")
                link(rel = "stylesheet", href = "$PUBLIC_DIR/$timeStampString/plan/js/bootstrap.min.css", type = "text/css")
                title("Lageplan Friedhof Pattensen")
            }
            body(classes = "container py-4") {
                h1 { +"Lageplan Friedhof Pattensen" }
                p { +"Stand: $dueDate" }
                ul {
                    li { a(href = "$INTERNAL_DIR/$timeStampString/Friedhofsplan.html") { +"Interne Version" } }
                    li { a(href = "$PUBLIC_DIR/$timeStampString/Friedhofsplan.html") { +"Öffentliche Version" } }
                }
            }
        }
        writeFile(File(planDir, "index.html"), html)
    }

    private fun authConfig(users: List<String>) = """
        |AuthType Basic
        |AuthName "$authName"
        |AuthUserFile ${config.authUserFile}
        |Require user ${users.distinct().joinToString(" ")}
        |""".trimMargin()

    private fun escapeRegex(value: String) = value.replace(Regex("[^A-Za-z0-9_-]"), "\\\\$0")

    private fun writeFile(file: File, content: String) {
        file.parentFile.mkdirs()
        file.writeText(content)
    }

    companion object {
        private val HTTPS_REDIRECT = """
            |RewriteCond %{HTTPS} !=on
            |RewriteRule ^ https://%{HTTP_HOST}%{REQUEST_URI} [R=301,L]""".trimMargin()
    }
}
