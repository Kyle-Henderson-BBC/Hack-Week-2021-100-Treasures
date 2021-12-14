package com.example.hack_week_100_treasures

import kotlin.random.Random

class CharactersRepository {

    private val gameCharacters: MutableList<Treasure> = mutableListOf()

    private val chars: MutableList<String> = mutableListOf()

    init {
       for (count in 1..100){
           chars.add("person $count")
       }
    }

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