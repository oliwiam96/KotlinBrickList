package com.oliwia.bricksapp

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.nio.file.Files.size
import java.text.SimpleDateFormat
import android.widget.CompoundButton
import java.util.*


/**
 * Created by Oliwia on 25.05.2018.
 */
class MyAdapter(var parentView: View, val dbHandler: MyDBHandler, private val context: Context) : BaseAdapter(), ListAdapter {
    var list: MutableList<Inventory> = mutableListOf<Inventory>()

    init {
        list = dbHandler.getInventoriesListOnlyActive()
    }

    fun addNewInventory(inventory: Inventory){
        dbHandler.addInventoryWithParts(inventory)
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
        var inventory = list[position]
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
        listItemTextDateAccessed.text = "Last Accessed: " + simpleDateFormat.format(list[position].lastAccessed)

        //Handle buttons and add onClickListeners
        val deleteBtn = view.findViewById<Button>(R.id.buttonDelete)

        deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //do something
                dbHandler.deleteInventoryWithParts(inventory)
                list.removeAt(position) //or some other task
                notifyDataSetChanged()
            }
        })

        val archivedSwitch = view.findViewById<Switch>(R.id.switchArchived)
        archivedSwitch.isChecked = (inventory.active == 1)
        archivedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            run {
                inventory = list[position] // WARNING!!! we must update inventory
                val showArchivedSwitch = parentView.findViewById<Switch>(R.id.switchShowArchived)
                if(isChecked){
                    inventory.active = 1
                } else{
                    inventory.active = 0
                }
                dbHandler.updateInventory(inventory)
                if(inventory.active == 0 && !showArchivedSwitch.isChecked){
                    list.removeAt(position) //or some other task
                }
                notifyDataSetChanged()
            }
        }
        val showArchivedSwitch = parentView.findViewById<Switch>(R.id.switchShowArchived)
        showArchivedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                if(isChecked){
                    list = dbHandler.getInventoriesList()
                } else{
                    list = dbHandler.getInventoriesListOnlyActive()
                }
                notifyDataSetChanged()
            }
        }

        return view
    }

}