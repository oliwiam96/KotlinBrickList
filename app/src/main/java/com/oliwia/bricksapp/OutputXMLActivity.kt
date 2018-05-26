package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.ArrayAdapter





class OutputXMLActivity : AppCompatActivity() {

    // TODO type as enum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_output_xml)

        val dropdown = findViewById<Spinner>(R.id.mySpinner)
        val dbHandler = MyDBHandler(this)
        dbHandler.openDataBase()
        var list = dbHandler.getInventoriesList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        dropdown.adapter = adapter
        dbHandler.close()

        val dropdownType = findViewById<Spinner>(R.id.mySpinnerType)
        var listType = arrayOf("ONLY NEW", "ONLY USED", "NEVERMIND");
        val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listType)
        dropdownType.adapter = adapterType

    }

    override fun finish() {
        val dropdown = findViewById<Spinner>(R.id.mySpinner)
        var inventory = dropdown.selectedItem as Inventory
        val id = inventory.id
        val dropdownType = findViewById<Spinner>(R.id.mySpinnerType)
        val type = dropdownType.selectedItem as String

        val data = Intent()
        data.putExtra("inventoryId", id)
        data.putExtra("type", type)
        setResult(Activity.RESULT_OK, data)
        super.finish()
    }
    fun finishActivity(v: View){
        finish()
    }
}
