package ru.yole.jkid

import ru.yole.jkid.deserialization.*
import ru.yole.jkid.serialization.serialize
import java.io.StringReader
import java.util.*

interface Company {
    val name: String
}

data class CompanyImpl(override val name: String) : Company

data class User(
    val name: String,
    val age: Int,
    val address: Address,
    val leftAddress: List<Address>,
    @DateFormat("yyyy-MM-dd") val birthDay: Date,
    val hobby: List<Int>,
    @DeserializeInterface(CompanyImpl::class) val company: Company,
    val bookPrice: Map<String, Double>
)

data class Address(val city: String)


private fun testSerialize() {
    val user = User(
        "张三",
        30,
        address = Address(city = "上海"),
        leftAddress = listOf(Address(city = "洛阳")),
        birthDay = Date(),
        hobby = listOf(1, 2, 3),
        company = CompanyImpl(name = "apk"),
        bookPrice = mapOf("Catch-22" to 10.92, "The Lord of the Rings" to 11.49)
    )
    val json = serialize(user)
    println(json)
}

private fun testCharReader() {
    val charReader = CharReader(StringReader("{\"key\": \"value\"}"))

    try {
        charReader.expectText("{", setOf('"'))
        println(charReader.readNextChars(3)) // 输出：key
    } catch (e: MalformedJSONException) {
        println("Error: ${e.message}")
    }
}

private fun testLexer() {
    val lexer = Lexer(StringReader("""{"key": "value", "number": 123.45, "boolean": true}"""))
    while (true) {
        val token = lexer.nextToken() ?: break
        println(token)
    }
}

private fun testClassInfoCache() {
    data class Person(
        @JsonName("full_name") val name: String,
        val age: Int
    )

    val cache = ClassInfoCache()
    val classInfo = cache[Person::class]

    val arguments = mapOf(
        classInfo.getConstructorParameter("full_name") to "John Doe",
        classInfo.getConstructorParameter("age") to 30
    )

    val person = classInfo.createInstance(arguments)
    println(person) // 输出：Person(name=John Doe, age=30)

}

private fun testDeserializer() {

    val user0 = User(
        "张三",
        30,
        address = Address(city = "上海"),
        leftAddress = listOf(Address(city = "洛阳")),
        birthDay = Date(),
        hobby = listOf(1, 2, 3),
        company = CompanyImpl(name = "apk"),
        bookPrice = mapOf("Catch-22" to 10.92, "The Lord of the Rings" to 11.49)
    )
    val json = serialize(user0)
    println(json)

    val lexer = Lexer(StringReader(json))

    while (true) {
        val token = lexer.nextToken() ?: break
        println(token)
    }
    println("")
    val user = deserialize<User>(json)
    println(user)
}

data class BookStore(val bookPrice: Map<String, Double>)

private fun testMap() {
    val bookStore = BookStore(mapOf("Catch-22" to 10.92, "The Lord of the Rings" to 11.49))
    val json = serialize(bookStore)
    println(json)
    val bookStoreD = deserialize<BookStore>(json)
    println(bookStoreD)
}

fun main() {
    testDeserializer()
}
