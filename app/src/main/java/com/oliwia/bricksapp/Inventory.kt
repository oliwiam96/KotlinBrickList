package com.oliwia.bricksapp

import java.time.LocalDateTime
import java.util.*

/**
 * Created by Oliwia on 24.05.2018.
 */

class Inventory{

    var id: Int = 0
    var name: String = ""
    var active: Boolean = true
    var lastAccessed:Date = Date()
    //lastAccessed.time

    constructor(name: String) {
        this.name = name
    }
}
