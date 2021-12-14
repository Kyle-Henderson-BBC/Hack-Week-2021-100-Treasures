package com.example.hack_week_100_treasures

import kotlin.random.Random

class CharactersRepository {

    private val gameCharacters: MutableList<Treasure> = mutableListOf()

    val chars: MutableList<String> = mutableListOf("person 1", "person 2", "person 3", "person 4")

    fun fetchGuesses(): List<Treasure> {
        return gameCharacters
    }

    fun addGuess(treasure: Treasure){
        gameCharacters.add(treasure)
    }

    fun getRandomGuess(): Treasure {
        val randomIndex = Random.nextInt(gameCharacters.size)
        return gameCharacters[randomIndex]
    }
    fun getRandomString(): String {
        val randomIndex = Random.nextInt(chars.size)
        return chars[randomIndex]
    }
}