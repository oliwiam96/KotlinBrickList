package com.oliwia.bricksapp

import android.graphics.Bitmap

/**
 * Created by Oliwia on 24.05.2018.
 */
class InventoryPart {
    // input XML
    var ITEMTYPE = "" // output XML
    var ITEMID = "" // output XML
    var QTY = 0
    var COLOR = 0 // output XML
    var EXTRA = ""

    // table in a database
    var id: Long = 0
    var inventoryID: Long = 0
    var typeID: Int = 0
    var itemID: Int = 0
    var quantityInSet: Int = 0
    var quantityInStore: Int = 0
    var colorID: Int = 0
    var extra: Int = 0

    // GUI description
    var colorName: String = ""
    var itemTypeName: String = ""
    var partName: String = ""
    var codesCode: Int? = null
    var image: Bitmap? = null

    fun getURLString(): String {
        if (codesCode != null) {
            return "https://www.lego.com/service/bricks/5/2/" + codesCode.toString()
        } else {
            if (ITEMTYPE == "M") {
                return "https://img.bricklink.com/ItemImage/MN/" + COLOR.toString() + "/" + ITEMID + ".png"
            } else {
                return "http://img.bricklink.com/P/" + COLOR.toString() + "/" + ITEMID + ".gif"
            }
        }
    }
}