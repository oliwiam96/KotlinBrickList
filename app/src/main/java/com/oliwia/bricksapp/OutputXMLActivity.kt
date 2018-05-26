package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.ArrayAdapter





class OutputXMLActivity : AppCompatActivity() {

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
    }

    override fun finish() {
        val dropdown = findViewById<Spinner>(R.id.mySpinner)
        var inventory = dropdown.selectedItem as Inventory
        val id = inventory.id
        val data = Intent()
        data.putExtra("inventoryId", id)
        setResult(Activity.RESULT_OK, data)
        super.finish()
    }
    fun finishActivity(v: View){
        finish()
    }
}
