package com.oliwia.bricksapp

import java.util.*

/**
 * Created by Oliwia on 24.05.2018.
 */

class Inventory{

    var id: Long = 0
    var name: String = ""
    var active: Int = 1
    var lastAccessed:Date = Date()
    //lastAccessed.time

    var parts: MutableList<InventoryPart> = mutableListOf<InventoryPart>()

    override fun toString(): String {
        return name.toString()
    }

}
