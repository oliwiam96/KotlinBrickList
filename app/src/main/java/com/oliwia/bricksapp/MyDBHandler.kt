package com.oliwia.bricksapp


import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException
import java.util.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


/**
 * Created by Oliwia on 24.05.2018.
 */

class MyDBHandler(private val myContext: Context) : SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private var myDataBase: SQLiteDatabase? = null


    companion object {
        //The Android's default system path of your application database.
        private val DB_PATH = "/data/data/com.oliwia.bricksapp/databases/"
        private val DB_NAME = "BrickList.db"
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

        } catch (e: SQLiteException) {
            //database does't exist yet.
        }
        return if (checkDB != null) {
            checkDB.close()
            true
        } else {
            false
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext.assets.open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        length = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBaseIfDoesNotExist() {

        val dbExist = checkDataBase()

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase
            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    @Throws(SQLException::class)
    fun openDataBase() {
        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)

    }

    @Synchronized override fun close() {
        if (myDataBase != null)
            myDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun exampleSelect() {
        val query = "SELECT NAME FROM PARTS WHERE CODE = 3001"
        var cursor = myDataBase!!.rawQuery(query, null)
        var name = ""
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
    }

    private fun setFieldsForPartFromXMLFields(inventoryPart: InventoryPart) {

        val queryTYPEID = "SELECT _ID FROM ITEMTYPES WHERE CODE = ?"
        var cursorTYPEID = myDataBase!!.rawQuery(queryTYPEID, arrayOf(inventoryPart.ITEMTYPE))
        var typeID = 0
        if (cursorTYPEID.moveToFirst()) {
            typeID = cursorTYPEID.getInt(0)
        }
        cursorTYPEID.close()
        inventoryPart.typeID = typeID

        val queryITEMID = "SELECT _ID FROM PARTS WHERE CODE = ?"
        var cursorITEMID = myDataBase!!.rawQuery(queryITEMID, arrayOf(inventoryPart.ITEMID))
        var itemID = 0
        if (cursorITEMID.moveToFirst()) {
            itemID = cursorITEMID.getInt(0)
        } else {
            // there wasn't such a row- now we insert it
            val values = ContentValues()
            values.put("TYPEID", inventoryPart.typeID)
            values.put("CODE", inventoryPart.ITEMID)
            values.put("NAME", "unknown")
            values.put("CATEGORYID", 1)
            var id = myDataBase!!.insert("PARTS", null, values)
            itemID = id.toInt()
        }
        cursorITEMID.close()
        inventoryPart.itemID = itemID

        inventoryPart.quantityInSet = inventoryPart.QTY

        val queryCOLOR = "SELECT _ID FROM COLORS WHERE CODE = ?"
        var cursorCOLOR = myDataBase!!.rawQuery(queryCOLOR, arrayOf(inventoryPart.COLOR.toString()))
        var colorID = 0
        if (cursorCOLOR.moveToFirst()) {
            colorID = cursorCOLOR.getInt(0)
        }
        cursorCOLOR.close()
        inventoryPart.colorID = colorID

        inventoryPart.extra = if (inventoryPart.EXTRA == "Y") 1 else 0
    }

    private fun setFieldsForPartsFromXMLFields(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            setFieldsForPartFromXMLFields(inventoryPart)
        }
    }

    private fun addInventory(inventory: Inventory) {
        val values = ContentValues()
        values.put("NAME", inventory.name)
        values.put("ACTIVE", inventory.active)
        values.put("LASTACCESSED", inventory.lastAccessed.time)
        var id = myDataBase!!.insert("INVENTORIES", null, values)
        inventory.id = id
    }

    private fun setInventoryIdForParts(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            inventoryPart.inventoryID = inventory.id
        }
    }

    private fun addPart(inventoryPart: InventoryPart) {
        val values = ContentValues()
        values.put("INVENTORYID", inventoryPart.inventoryID)
        values.put("TYPEID", inventoryPart.typeID)
        values.put("ITEMID", inventoryPart.itemID)
        values.put("QUANTITYINSET", inventoryPart.quantityInSet)
        values.put("QUANTITYINSTORE", inventoryPart.quantityInStore)
        values.put("COLORID", inventoryPart.colorID)
        values.put("EXTRA", inventoryPart.extra)
        var id = myDataBase!!.insert("INVENTORIESPARTS", null, values)
        inventoryPart.id = id
    }

    private fun addParts(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            addPart(inventoryPart)
        }
    }

    private fun setDescFieldsForPart(inventoryPart: InventoryPart) {
        // set COLOR and colorName
        val query = "SELECT CODE, IFNULL(NAMEPL, NAME) FROM COLORS WHERE _ID = ?"
        var cursor = myDataBase!!.rawQuery(query, arrayOf(inventoryPart.colorID.toString()))
        if (cursor.moveToFirst()) {
            inventoryPart.COLOR = cursor.getInt(0)
            inventoryPart.colorName = cursor.getString(1)
        }
        cursor.close()


        // set ITEMTYPE and itemTypeName
        val query2 = "SELECT CODE, IFNULL(NAMEPL, NAME) FROM ITEMTYPES WHERE _ID = ?"
        var cursor2 = myDataBase!!.rawQuery(query2, arrayOf(inventoryPart.typeID.toString()))
        if (cursor2.moveToFirst()) {
            inventoryPart.ITEMTYPE = cursor2.getString(0)
            inventoryPart.itemTypeName = cursor2.getString(1)
        }
        cursor2.close()

        // set ITEMID and partName
        val query3 = "SELECT CODE, IFNULL(NAMEPL, NAME) FROM PARTS WHERE _ID = ?"
        var cursor3 = myDataBase!!.rawQuery(query3, arrayOf(inventoryPart.itemID.toString()))
        if (cursor3.moveToFirst()) {
            inventoryPart.ITEMID = cursor3.getString(0)
            inventoryPart.partName = cursor3.getString(1)
        }
        cursor3.close()

        // set codesCode if possible
        val query4 = "SELECT CODE FROM CODES WHERE ITEMID = ? AND COLORID = ?"
        var cursor4 = myDataBase!!.rawQuery(query4, arrayOf(inventoryPart.itemID.toString(), inventoryPart.colorID.toString()))
        if (cursor4.moveToFirst()) {
            inventoryPart.codesCode = cursor4.getInt(0)
        }
        cursor4.close()

        // set image
        val query5 = "SELECT IMAGE FROM CODES WHERE ITEMID = ? AND COLORID = ?"
        var cursor5 = myDataBase!!.rawQuery(query5, arrayOf(inventoryPart.itemID.toString(), inventoryPart.colorID.toString()))
        if (cursor5.moveToFirst()) {
            val imgByte = cursor5.getBlob(0)
            if (imgByte != null) {
                inventoryPart.image = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
            }
        }
        cursor5.close()

    }


    private fun setDescFieldsForParts(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            setDescFieldsForPart(inventoryPart)
        }
    }

    fun addInventoryWithParts(inventory: Inventory) {
        addInventory(inventory)
        setInventoryIdForParts(inventory)
        setFieldsForPartsFromXMLFields(inventory)
        setDescFieldsForParts(inventory)
        addParts(inventory)
    }

    fun deleteInventoryWithParts(inventory: Inventory) {
        myDataBase!!.delete("INVENTORIESPARTS", "INVENTORYID = ?", arrayOf(inventory.id.toString()))
        myDataBase!!.delete("INVENTORIES", "_ID = ?", arrayOf(inventory.id.toString()))
    }

    /**
     * Updates ACTIVE and LASTASCESSED fields in INVENTORIES table
     */
    fun updateInventory(inventory: Inventory) {
        inventory.lastAccessed = Date()
        val values = ContentValues()
        values.put("ACTIVE", inventory.active)
        values.put("LASTACCESSED", inventory.lastAccessed.time)
        myDataBase!!.update("INVENTORIES", values, "_ID = ?", arrayOf(inventory.id.toString()))
    }

    /**
     * Updates QUANTITYINSTORE field in INVENTORIESPARTS table
     */
    fun updatePart(inventoryPart: InventoryPart) {
        val values = ContentValues()
        values.put("QUANTITYINSTORE", inventoryPart.quantityInStore)
        myDataBase!!.update("INVENTORIESPARTS", values, "_ID = ?", arrayOf(inventoryPart.id.toString()))
    }

    private fun updateParts(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            updatePart(inventoryPart)
        }
    }

    fun updateInventoryWithParts(inventory: Inventory) {
        updateInventory(inventory)
        updateParts(inventory)
    }

    private fun setPartsForInventory(inventory: Inventory) {
        val query = "SELECT _ID, INVENTORYID, TYPEID, ITEMID, QUANTITYINSET," +
                "QUANTITYINSTORE, COLORID, EXTRA " +
                "FROM INVENTORIESPARTS " +
                "WHERE INVENTORYID = ?"
        var cursor = myDataBase!!.rawQuery(query, arrayOf(inventory.id.toString()))

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                var inventoryPart = InventoryPart()
                inventoryPart.id = cursor.getLong(0)
                inventoryPart.inventoryID = cursor.getLong(1)  // == inventory.id
                inventoryPart.typeID = cursor.getInt(2)
                inventoryPart.itemID = cursor.getInt(3)
                inventoryPart.quantityInSet = cursor.getInt(4)
                inventoryPart.quantityInStore = cursor.getInt(5)
                inventoryPart.colorID = cursor.getInt(6)
                inventoryPart.extra = cursor.getInt(7)

                setDescFieldsForPart(inventoryPart)

                inventory.parts.add(inventoryPart)
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun getIventoriesListQuery(query: String): MutableList<Inventory>{
        var inventories: MutableList<Inventory> = mutableListOf<Inventory>()
        var cursor = myDataBase!!.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                var inventory = Inventory()
                inventory.id = cursor.getLong(0)
                inventory.name = cursor.getString(1)
                inventory.active = cursor.getInt(2)
                inventory.lastAccessed = Date(cursor.getLong(3))

                //setPartsForInventory(inventory) // DON'T DO IT FOR BETTER PERFORMANCE
                inventories.add(inventory)
                cursor.moveToNext()
            }
        }

        cursor.close()
        return inventories
    }

    fun getInventoriesList(): MutableList<Inventory> {
        val query = "SELECT _ID, NAME, ACTIVE, LASTACCESSED FROM INVENTORIES ORDER BY LASTACCESSED DESC"
        return getIventoriesListQuery(query)
    }

    fun getInventoriesListOnlyActive(): MutableList<Inventory> {
        val query = "SELECT _ID, NAME, ACTIVE, LASTACCESSED FROM INVENTORIES WHERE ACTIVE = 1 ORDER BY LASTACCESSED DESC"
        return getIventoriesListQuery(query)
    }

    fun getInventory(id: Long): Inventory {

        val query = "SELECT _ID, NAME, ACTIVE, LASTACCESSED FROM INVENTORIES WHERE _ID = ?"
        var cursor = myDataBase!!.rawQuery(query, arrayOf(id.toString()))
        var inventory = Inventory()

        if (cursor.moveToFirst()) {
            inventory.id = cursor.getLong(0)
            inventory.name = cursor.getString(1)
            inventory.active = cursor.getInt(2)
            inventory.lastAccessed = Date(cursor.getLong(3))

            setPartsForInventory(inventory)
        }

        cursor.close()
        return inventory
    }

    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }


    private fun saveImageForPart(inventoryPart: InventoryPart) {
        if (inventoryPart.image != null) {
            val query = "SELECT * FROM CODES WHERE ITEMID = ? AND COLORID = ?"
            var cursor = myDataBase!!.rawQuery(query, arrayOf(inventoryPart.itemID.toString(), inventoryPart.colorID.toString()))
            if (!cursor.moveToFirst()) {
                val values = ContentValues()
                values.put("ITEMID", inventoryPart.itemID)
                values.put("COLORID", inventoryPart.colorID)
                myDataBase!!.insert("CODES", null, values)
            }
            cursor.close()

            if (!imageInDatabse(inventoryPart)) {
                val values = ContentValues()
                values.put("IMAGE", getBitmapAsByteArray(inventoryPart.image!!))
                myDataBase!!.update("CODES", values, "ITEMID = ? AND COLORID = ?",
                        arrayOf(inventoryPart.itemID.toString(), inventoryPart.colorID.toString()))
            }
        }
    }

    fun saveImagesForInventory(inventory: Inventory) {
        for (inventoryPart in inventory.parts) {
            saveImageForPart(inventoryPart)
        }
    }

    fun imageInDatabse(inventoryPart: InventoryPart): Boolean {
        val query = "SELECT IMAGE FROM CODES WHERE ITEMID = ? AND COLORID = ?"
        var cursor = myDataBase!!.rawQuery(query, arrayOf(inventoryPart.itemID.toString(), inventoryPart.colorID.toString()))
        var found = false
        if (cursor.moveToFirst()) {
            val imgByte = cursor.getBlob(0)
            found = (imgByte != null)
        }
        cursor.close()
        return found
    }


}