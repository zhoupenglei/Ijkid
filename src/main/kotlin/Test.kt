package ru.yole.jkid

import ru.yole.jkid.deserialization.*
import ru.yole.jkid.deserialization.CharReader
import ru.yole.jkid.serialization.serialize
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date

interface Company {
    val name: String
}

data class CompanyImpl(override val name: String) : Company

data class User(
    val name: String,
    val age: Int,
    val address: Address,
    val leftAddress: List<Address>,
    @CustomSerializer(DateSerializer::class) val birthDay: Date,
    val hobby: List<Int>,
    @DeserializeInterface(CompanyImpl::class) val company: Company
)

data class Address(val city: String)

object DateSerializer : ValueSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun toJsonValue(value: Date): Any? =
        dateFormat.format(value)

    override fun fromJsonValue(jsonValue: Any?): Date =
        dateFormat.parse(jsonValue as String)
}

private fun testSerialize() {
    val user = User(
        "张三",
        30,
        address = Address(city = "上海"),
        leftAddress = listOf(Address(city = "洛阳")),
        birthDay = Date(),
        hobby = listOf(1, 2, 3),
        company = CompanyImpl(name = "apk")
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
        company = CompanyImpl(name = "apk")
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

fun main() {
    testDeserializer()
}
