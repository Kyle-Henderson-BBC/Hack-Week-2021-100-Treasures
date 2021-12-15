package com.example.hack_week_100_treasures

import kotlin.random.Random

class CharactersRepository {

    // TODO: this list exists only as MVP hack values
    private val hardCodedGameCharacters: List<Character> = listOf(
        Character("David Attenborough", null),
        Character("Doctor Who", null),
        Character("Hacker T. Dog", null),
        Character("Villanelle (Killing Eve)", null),
        Character("Wallace and Gromit", null),
        Character("Terry Wogan", null),
        Character("Dirty Den (Den Watts)", null),
        Character("Basil Brush", null),
        Character("Owain Wyn Evans", null),
        Character("Fiona Bruce", null),
        Character("Oti Mabuse", null),
        Character("Hey Duggee", null),
        Character("Graham Norton", null),
        Character("Gary Linnekar", null),
    )

    private var availableCharacters: MutableList<Character> = hardCodedGameCharacters.toMutableList()

    fun resetAvailableCharacters() {
        // TODO: this will be replaced with a fetch from network or local database
        availableCharacters = hardCodedGameCharacters.toMutableList()
    }

    fun fetchAvailableCharacters(): List<Character> {
        return availableCharacters
    }

    fun addCharacter(character: Character){
        availableCharacters.add(character)
    }

    fun getNextCharacterAndConsume(): Character {
        val randomIndex = Random.nextInt(availableCharacters.size)
        val character = availableCharacters[randomIndex]
        availableCharacters.removeAt(randomIndex)

        if(availableCharacters.size < 1) resetAvailableCharacters()
        return character
    }
}