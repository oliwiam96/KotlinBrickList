package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    var prefix  = ""
    var extension = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val extras = intent.extras ?: return
        prefix = extras.getString("prefix")
        extension = extras.getString("extension")
        editTextPrefix.setText(prefix)
        editTextExtension.setText(extension)
    }

    override fun finish() {
        // if OK wasn't pressed, prefix and extension remain the same as they were on create
        val data = Intent()
        data.putExtra("prefix", prefix)
        data.putExtra("extension", extension)
        setResult(Activity.RESULT_OK, data)
        super.finish()
    }

    fun saveAndFinish(v: View){
        prefix = editTextPrefix.text.toString()
        extension = editTextExtension.text.toString()
        finish()
    }
}
