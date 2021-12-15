package com.example.hack_week_100_treasures

import androidx.annotation.RawRes

data class Character(val name: String,
                     @RawRes val soundClueId: Int?,
                     val imageId: Int? = null,
                     val desc: String = "This is a content description"
                    )
