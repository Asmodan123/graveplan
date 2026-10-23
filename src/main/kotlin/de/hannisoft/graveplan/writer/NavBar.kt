package de.hannisoft.graveplan.writer

import kotlinx.html.HEAD
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title

/** Start page of the website, the login dialog appears again when it is called after a logout. */
private const val LOGOUT_TARGET = "/$PLAN_DIR/"

/** @param resourceDir relative path to the copied resources of the plan directory, e.g. "../plan" */
fun HEAD.logoutScript(resourceDir: String) {
    script(src = "$resourceDir/logout.js") {}
}

/** Has to be added before the other right floating items to be shown on the very right. */
fun UL.logoutItem(resourceDir: String) {
    li {
        style = "float: right"
        a(href = LOGOUT_TARGET) {
            title = "Abmelden"
            attributes["onclick"] = "return logout('$LOGOUT_TARGET')"
            img(alt = "Abmelden", src = "$resourceDir/logout.svg", classes = "icon")
        }
    }
}
