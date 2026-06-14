package de.hannisoft.graveplan.model

data class Owner(
    var salutation: String? = null,
    var salutationLetter: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var street: String? = null,
    var zipAndTown: String? = null
) {
    override fun toString(): String = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
}