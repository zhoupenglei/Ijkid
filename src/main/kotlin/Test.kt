package ru.yole.jkid

import ru.yole.jkid.serialization.serialize
import java.text.SimpleDateFormat
import java.util.Date


data class User(
    val name: String,
    val age: Int,
    val address: Address,
    @CustomSerializer(DateSerializer::class) val birthDay: Date
)

data class Address(val city: String)

object DateSerializer : ValueSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun toJsonValue(value: Date): Any? =
        dateFormat.format(value)

    override fun fromJsonValue(jsonValue: Any?): Date =
        dateFormat.parse(jsonValue as String)
}

fun main() {
    val user = User("张三", 30, address = Address(city = "上海"), birthDay = Date())
    val json = serialize(user)
    println(json)
}
