package ru.yole.jkid

import ru.yole.jkid.serialization.serialize


data class User(val name: String, val age: Int, val address: Address)
data class Address(val city: String)

fun main() {
    val user = User("张三", 30, address = Address(city = "上海"))
    val json = serialize(user)
    println(json)
}
