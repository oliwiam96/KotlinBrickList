package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.widget.ListView
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    val REQUEST_CODE = 10000
    val REQUEST_CODE_SETTINGS = 10001
    val REQUEST_CODE_NEW_PROJECT = 10002
    val REQUEST_CODE_DETAILS = 10003
    var prefix = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    var extension = ".xml"
    var newInventoryNumber = ""
    var newInventoryName = ""
    var myAdapter:MyAdapter? = null
    val dbHandler = MyDBHandler(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        dbHandler.createDataBaseIfDoesNotExist()
        dbHandler.openDataBase()
        myAdapter = MyAdapter(findViewById(android.R.id.content), dbHandler, this, REQUEST_CODE_DETAILS)

        val lView = findViewById<ListView>(R.id.myListView)
        lView.adapter = myAdapter

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                startSettingsActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == REQUEST_CODE)
                && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                if (data.hasExtra("colorInt")) {
                    var colorInt = data.extras.getString("colorInt").toInt()
                }
                if (data.hasExtra("precision")) {
                    var precision = data.extras.getString("precision").toInt()
                }

            }
        } else if ((requestCode == REQUEST_CODE_SETTINGS)
                && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                if (data.hasExtra("prefix")) {
                    prefix = data.extras.getString("prefix")
                }
                if (data.hasExtra("extension")) {
                    extension = data.extras.getString("extension")
                }
            }

        } else if ((requestCode == REQUEST_CODE_NEW_PROJECT)
                && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                if (data.hasExtra("projectName") && data.hasExtra("inventoryNumber")) {
                    var projectName = data.extras.getString("projectName")
                    var inventoryNumber = data.extras.getString("inventoryNumber")
                    if (projectName.isNotEmpty() && inventoryNumber.isNotEmpty()) {
                        newInventoryName = projectName
                        newInventoryNumber = inventoryNumber
                        XMLDownloader().execute()
                    }
                }
            }
        }
        nav_view.setCheckedItem(R.id.nav_myProjects)
    }


    private fun startNewProjectActivity() {
        val i = Intent(this, NewProjectActivity::class.java)

        startActivityForResult(i, REQUEST_CODE_NEW_PROJECT)
    }


    private fun startSettingsActivity() {
        val i = Intent(this, SettingsActivity::class.java)
        i.putExtra("prefix", prefix)
        i.putExtra("extension", extension)
        startActivityForResult(i, REQUEST_CODE_SETTINGS)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_add -> {
                /*
                 Toast.makeText(this, "Oliwia Masian\n127324", Toast.LENGTH_LONG).show()
                 val i = Intent(this, AddActivity::class.java)
                 i.putExtra("Parametr", "Twoje dane")
                 startActivityForResult(i, REQUEST_CODE)*/
                startNewProjectActivity()

            }
            R.id.nav_myProjects -> {
                var list = dbHandler.getInventoriesList()
                //helloView.text = list.size.toString()

                //imageImage.setImageBitmap(list[0].parts[2].image)

            }
            R.id.nav_settings -> {
                startSettingsActivity()
            }
            R.id.nav_save -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    private inner class ImageDownloader : AsyncTask<String, Int, String>() {
        val noImageURLString = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQaopXXLbLGXDFcnzgeoPisO5gB98_YORuu3YqA8vYeryZ0-2Nyfw"
        var inventory:Inventory? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            dbHandler.saveImagesForInventory(inventory!!)
        }

        override fun doInBackground(vararg p0: String?): String {
            var inventoryID = p0[0]
            inventory = dbHandler.getInventory(inventoryID!!.toLong())
            for (inventoryPart in inventory!!.parts) {
                if(!dbHandler.imageInDatabse(inventoryPart)){
                    setImageForPart(inventoryPart)
                }

            }
            return "success"
        }

        private fun setImageForPart(inventoryPart: InventoryPart) {
            var failed = false
            var bitmap: Bitmap? = null
            var URLString = inventoryPart.getURLString()
            try {
                bitmap = BitmapFactory.decodeStream(URL(URLString).content as InputStream)
            } catch (ex: IOException) {
                    failed = true
            }
            if(!failed){
                inventoryPart.image = bitmap
            }

        }
    }

    private inner class XMLDownloader : AsyncTask<String, Int, String>() {

        var failed = false
        var inventory: Inventory? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (failed) {
                Toast.makeText(this@MainActivity, "Inventory with a given number not found", Toast.LENGTH_LONG).show()
                //helloView.text = "FAIL"
            } else {
                //helloView.text = "SUCCESS"
                myAdapter!!.addNewInventory(inventory!!)
                ImageDownloader().execute(inventory!!.id.toString())
            }
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val url = URL(prefix + newInventoryNumber + extension)
                val result = url.openStream()
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().
                        newDocumentBuilder().parse(result)
                xmlDoc.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("ITEM")
                inventory = Inventory()
                inventory!!.name = newInventoryName
                for (i in 0..items.length - 1) {
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        var inventoryPart = InventoryPart()
                        var shouldBeIncluded = false
                        (0..children.length - 1)
                                .map { children.item(it) }
                                .filterIsInstance<Element>()
                                .forEach {
                                    when (it.nodeName) {
                                        "ITEMTYPE" -> {
                                            inventoryPart.ITEMTYPE = it.textContent
                                        }
                                        "ITEMID" -> {
                                            inventoryPart.ITEMID = it.textContent
                                        }
                                        "QTY" -> {
                                            inventoryPart.QTY = it.textContent.toInt()
                                        }
                                        "COLOR" -> {
                                            inventoryPart.COLOR = it.textContent.toInt()
                                        }
                                        "EXTRA" -> {
                                            inventoryPart.EXTRA = it.textContent
                                        }
                                        "ALTERNATE" -> {
                                            if (it.textContent == "N") {
                                                shouldBeIncluded = true
                                            }
                                        }
                                    }
                                }
                        if (shouldBeIncluded) {
                            inventory!!.parts.add(inventoryPart)
                        }
                    }
                }
            } catch (ex: IOException) {
                failed = true
            }
            return "success"
        }
    }
}
