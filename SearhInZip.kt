import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile


fun main() {
//    val dir = File("./resources")
    val zipfile = File("./resources/A19 Timer.zip")

//    val searchFor = "flwTime.*days.*"
//    val searchFor = "flwTimeUtils.now()"
    val searchParams = SearchParams(
        searchText = "flwTimeUtils.now()",
        searchRegex = "flwTime.*days.*",
        useRegex = false,
        exact = false
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
            val inputStream: InputStream = zipFile.getInputStream(entry)
            val jsonStr = inputStream.use { instr ->
                instr.bufferedReader().use { it.readText() }
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
    if (searchParams.useRegex) {
        val searchFor = searchParams.searchRegex
        val regex = Regex(searchFor)
//        return searchIn.contains(regex)
        return regex.containsMatchIn(searchIn)
    }
    val searchFor = searchParams.searchText
    if (searchParams.exact) {
        return searchIn == searchFor
    }
    return searchIn.contains(searchFor)
}

data class SearchParams(val searchText: String, val searchRegex: String, val useRegex: Boolean, val exact: Boolean)