import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main() {
    val expr = "{{}}"
    val activeNode = ""
    val json = ObjectMapper().readTree(File("./resources/temp01.json"))

    // "}", ")", ",", "]", "+", "-", "*", "/", " ", "=", "&", "|", "%", ":", "?", "<", ">"

    var index = 0
    var isValid = true

    var outExpr = expr

    val regex = Regex("(!)?([a-zA-Z0-9.]+)[ }),\\]+\\-*/=&|%:?<>]")

    while (isValid) {
        val res = regex.containsMatchIn(expr)
        val match = regex.find(expr, index)

        isValid = match?.let {
            val neg = it.groupValues[1]
            var path = it.groupValues[2]
            if (!path.startsWith("root")) {
                path = "$activeNode.$path"
            }
            val jsonPath = path.replace(".", "/")
            val jsonValue = json.at("/$jsonPath")
            val value = when {
                jsonValue.isMissingNode -> "$path: N/A"
                jsonValue.isObject -> {
                    val empty = if (jsonValue.isEmpty) "EMPTY-" else ""
                    "${empty}OBJECT"
                }
                jsonValue.isArray -> {
                    val empty = if (jsonValue.isEmpty) "EMPTY-" else ""
                    "${empty}ARRAY"
                }
                else -> "$jsonValue"
            }
            outExpr = outExpr.replaceFirst(path, value)
            println("$path: $value")
            index = it.range.last
            true
        } ?: false
    }

    println(outExpr)
}