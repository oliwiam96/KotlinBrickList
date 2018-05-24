package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_new_project.*

class NewProjectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
    }


    fun saveAndFinish(v: View){
        var projectName = editTextProjectName.text.toString()
        var inventoryNumber = editTextInventoryNumber.text.toString()
        val data = Intent()
        data.putExtra("projectName", projectName)
        data.putExtra("inventoryNumber", inventoryNumber)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
