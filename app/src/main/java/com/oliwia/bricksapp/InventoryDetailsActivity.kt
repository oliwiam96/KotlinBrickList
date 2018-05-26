package com.oliwia.bricksapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class InventoryDetailsActivity : AppCompatActivity() {
    var myDetailsAdapter:MyDetailsAdapterAdapter? = null
    var dbHandler: MyDBHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_details)

        val extras = intent.extras ?: return
        var inventoryId = extras.getLong("inventoryId")

        dbHandler = MyDBHandler(this)
        dbHandler!!.openDataBase()
        myDetailsAdapter = MyDetailsAdapterAdapter(inventoryId, dbHandler!!, this)

        val lView = findViewById<ListView>(R.id.myListViewDetails)
        lView.adapter = myDetailsAdapter
    }
}
