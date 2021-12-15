package com.example.hack_week_100_treasures

import androidx.annotation.IdRes

data class Character(val name: String,
                     @IdRes val soundClueId: Int?)
