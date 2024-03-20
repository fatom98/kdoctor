package api.classes

/**
 * Some summary about class
 *
 * Detailed explanation about the class
 */
class Kdoc(name: String, count: Int) {

    /**
     * API explanation of the integer property
     */
    val integerProperty = 3

    /**
     * API explanation of the double property
     */
    val doubleProperty = 4.0

    // a comment for the developers which should be ignored by api scanner
    val stringProperty = "string"

    /**
     * Second constructor
     */
    constructor(counts: Int) : this("", counts)

    /**
     * Dom mapping method
     */
    fun toDom(string: String, int: Int) {
        val hebele = ""
    }

}