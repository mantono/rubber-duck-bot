package com.mantono.rubberduck

import com.fasterxml.jackson.databind.JsonNode
import spark.Request
import spark.kotlin.Http
import spark.kotlin.ignite
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import com.fasterxml.jackson.databind.ObjectMapper



private val rand: Random = SecureRandom()

fun main(args: Array<String>)
{
    val port: Int = when(args.isNotEmpty())
    {
        true -> Integer.parseInt(args[0])
        false -> 80
    }

    val s: AtomicInteger = AtomicInteger(0)

    val http: Http = ignite().port(port)

    http.get("/rubberduck")
    {
        status(200)
        val body: JsonNode =  parse(this.request)

        when(body["type"].asText())
        {
            "url_verification" -> body["challenge"].asText()
            else -> {
                val add: Int = rand.nextInt(20)
                val total = s.addAndGet(add).also { if(it >= 100) s.set(0) }
                when(total >= 100)
                {
                    true -> makeSomeNoise()
                    false -> reactOn(this.request.body())
                }
            }
        }
    }
}

private fun parse(request: Request): JsonNode
{
    val objectMapper = ObjectMapper()
    return objectMapper.readTree(request.body())
}

val noises: Array<String> = arrayOf("quack!", "quaaaack", "quack quack", "uuhf...")

fun makeSomeNoise(): String = noises[rand.nextInt(noises.size)]

fun reactOn(input: String): String = when(input)
{
    ":heart:", "<3" -> ":heart:"
    else -> "a"
}


