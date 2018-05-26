package com.oliwia.bricksapp

import android.content.Context
import android.widget.TextView
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.nio.file.Files.size
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListAdapter
import java.text.SimpleDateFormat


/**
 * Created by Oliwia on 25.05.2018.
 */
class MyAdapter(private val context: Context) : BaseAdapter(), ListAdapter {
    val dbHandler = MyDBHandler(context)
    var list: MutableList<Inventory> = mutableListOf<Inventory>()

    init {
        dbHandler.createDataBaseIfDoesNotExist()
        dbHandler.openDataBase()
        list = dbHandler.getInventoriesList()
        dbHandler.close()
    }

    fun addNewInventory(inventory: Inventory){
        dbHandler.openDataBase()
        dbHandler.addInventoryWithParts(inventory)
        dbHandler.close()
        list.add(inventory)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(pos: Int): Inventory {
        return list[pos]
    }

    override fun getItemId(pos: Int): Long {
        return list[pos].id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.my_row, null)
        }

        //Handle TextView and display string from your list
        val listItemTextName = view!!.findViewById<TextView>(R.id.textViewName)
        listItemTextName.text = list[position].name

        val listItemTextDateAccessed = view!!.findViewById<TextView>(R.id.textViewDateAccessed)
        val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
        listItemTextDateAccessed.text = "Date Accessed: " + simpleDateFormat.format(list[position].lastAccessed)

        //Handle buttons and add onClickListeners
        val deleteBtn = view.findViewById<Button>(R.id.buttonDelete)

        deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //do something
                var inventory = list[position]
                dbHandler.openDataBase()
                dbHandler.deleteInventoryWithParts(inventory)
                dbHandler.close()
                list.removeAt(position) //or some other task
                notifyDataSetChanged()
            }
        })
        return view
    }
}