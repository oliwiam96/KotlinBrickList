package com.oliwia.bricksapp


import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.IntegerRes
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException

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
        val db = this.writableDatabase
        var cursor = myDataBase!!.rawQuery(query, null)
        var name = ""
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
            cursor.close()
        }
    }

    private fun setFieldsForPartFromXMLFields(inventoryPart: InventoryPart) {

        val queryTYPEID = "SELECT _ID FROM ITEMTYPES WHERE CODE = \"${inventoryPart.ITEMTYPE}\""
        var cursorTYPEID = myDataBase!!.rawQuery(queryTYPEID, null)
        var typeID = 0
        if (cursorTYPEID.moveToFirst()) {
            typeID = cursorTYPEID.getInt(0)
            cursorTYPEID.close()
        }
        inventoryPart.typeID = typeID

        val queryITEMID = "SELECT _ID FROM PARTS WHERE CODE = \"${inventoryPart.ITEMID}\""
        var cursorITEMID = myDataBase!!.rawQuery(queryITEMID, null)
        var itemID = 0
        if (cursorITEMID.moveToFirst()) {
            itemID = cursorITEMID.getInt(0)
            cursorITEMID.close()
        }
        inventoryPart.itemID = itemID

        inventoryPart.quantityInSet = inventoryPart.QTY

        val queryCOLOR = "SELECT _ID FROM COLORS WHERE CODE = ${inventoryPart.COLOR}"
        var cursorCOLOR = myDataBase!!.rawQuery(queryCOLOR, null)
        var colorID = 0
        if (cursorCOLOR.moveToFirst()) {
            colorID = cursorCOLOR.getInt(0)
            cursorCOLOR.close()
        }
        inventoryPart.colorID = colorID

        inventoryPart.extra = if (inventoryPart.EXTRA == "Y") 1 else 0
    }

    private fun addInventory(inventory: Inventory){
        val values = ContentValues()
        values.put("NAME", inventory.name)
        values.put("ACTIVE", inventory.active)
        values.put("LASTACCESSED", inventory.lastAccessed.time)
        var id = myDataBase!!.insert("INVENTORIES", null, values)
        inventory.id = id
    }

    private fun setInventoryIdForParts(inventory: Inventory){
        for(inventoryPart in inventory.parts){
            inventoryPart.inventoryID = inventory.id
        }
    }

    private fun addPart(inventoryPart: InventoryPart){
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

    private fun addParts(inventory: Inventory){
        for(inventoryPart in inventory.parts){
            addPart(inventoryPart)
        }
    }

    fun setXMLFields(inventory: Inventory){
        for(inventoryPart in inventory.parts){
            setFieldsForPartFromXMLFields(inventoryPart)
        }
    }

    fun addInventoryWithParts(inventory: Inventory){
        addInventory(inventory)
        setInventoryIdForParts(inventory)
        setXMLFields(inventory)
        addParts(inventory)
    }




}