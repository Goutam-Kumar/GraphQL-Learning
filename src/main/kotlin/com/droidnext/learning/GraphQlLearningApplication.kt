package com.droidnext.learning

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphQlLearningApplication

fun main(args: Array<String>) {
	val dotenv = dotenv()
	dotenv.entries().forEach {
		System.setProperty(it.key, it.value)
	}
	runApplication<GraphQlLearningApplication>(*args)
}
