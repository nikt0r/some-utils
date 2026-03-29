import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile


fun main() {
//    val dir = File("./resources")
    val zipfile = File("./resources/A19 Timer.zip")

    val searchParams = SearchParams(
        searchFor = "flwTimeUtils.now()",
//        searchFor = "flwTime.*days.*",
        searchType = SerachType.TEXT
    )

//    dir.walkTopDown()
////        .filter { it.name.endsWith(".json") && it.readText().contains(searchFor) }
//        .filter { it.name.endsWith(".json") }
//        .forEach { file ->
//            println("${file.parentFile} ${file.name}")
//            findInFile(file, searchFor, searchParams)
//        }

    val zipFile = ZipFile(zipfile)
    zipFile.entries().asSequence().forEach { entry ->
        if (entry.name.endsWith(".json")) {
            val jsonStr = zipFile.getInputStream(entry).use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }
            findInText(File(entry.name).nameWithoutExtension, jsonStr, searchParams)
        }
    }
}

//fun findInFile(filename: String, file: File, searchFor: String, searchParams: SearchParams) {
//    val json = ObjectMapper().readTree(file)
//    findValue(filename, json, searchFor, searchParams)
//}

fun findInText(filename: String, text: String, searchParams: SearchParams) {
    val json = ObjectMapper().readTree(text)
    findValue(filename, json, searchParams)
}

fun findValue(filename: String, jsonNode: JsonNode, searchParams: SearchParams) {
    if (jsonNode.isObject) {
        val values = jsonNode.values()
        values.forEach {
            findValue(filename, it, searchParams)
        }
    } else if (jsonNode.isArray) {
        val array = jsonNode as ArrayNode
        array.forEach { findValue(filename, it, searchParams) }
    } else if (contains(jsonNode.asText(), searchParams)) {
        println("${filename.padEnd(20)} ${jsonNode.asText()}")
    }
}

fun contains(searchIn: String, searchParams: SearchParams): Boolean {
    val searchFor = searchParams.searchFor
    when (searchParams.searchType) {
        SerachType.REGEX -> {
            val regex = Regex(searchFor)
//        return searchIn.contains(regex)
            return regex.containsMatchIn(searchIn)
        }
        SerachType.EXACTTEXT -> {
            return searchIn.equals(searchFor, ignoreCase = false)
        }
        SerachType.EXACTTEXT_CASE_SENSITIVE -> {
            return searchIn.equals(searchFor, ignoreCase = true)
        }
        SerachType.TEXT_CASE_SENSITIVE -> {
            return searchIn.contains(searchFor, ignoreCase = false)
        }
        else -> {
            return searchIn.contains(searchFor, ignoreCase = true)
        }
    }
}

data class SearchParams(val searchFor: String, val searchType: SerachType)
enum class SerachType {
    REGEX,
    TEXT,
    TEXT_CASE_SENSITIVE,
    EXACTTEXT,
    EXACTTEXT_CASE_SENSITIVE
}