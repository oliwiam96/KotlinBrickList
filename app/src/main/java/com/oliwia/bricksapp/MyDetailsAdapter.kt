package com.oliwia.bricksapp

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.nio.file.Files.size
import java.text.SimpleDateFormat
import android.widget.CompoundButton
import java.util.*
import android.widget.RelativeLayout




/**
 * Created by Oliwia on 26.05.2018.
 */
class MyDetailsAdapterAdapter(var inventoryId: Long, val dbHandler: MyDBHandler,
                              private val context: Context) : BaseAdapter(), ListAdapter {
    var list: MutableList<InventoryPart> = mutableListOf<InventoryPart>()

    init {
        var inventory = dbHandler.getInventory(inventoryId)
        list = inventory.parts
    }


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(pos: Int): InventoryPart {
        return list[pos]
    }

    override fun getItemId(pos: Int): Long {
        return list[pos].id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.my_row_details, null)
        }

        //Handle TextView and display string from your list
        val listItemTextName = view!!.findViewById<TextView>(R.id.textViewNamePart)
        listItemTextName.text = list[position].partName

        val listItemTextITEMID = view.findViewById<TextView>(R.id.textViewITEMID)
        listItemTextITEMID.text = list[position].ITEMID + ", " + list[position].itemTypeName

        val listItemTextColorName = view.findViewById<TextView>(R.id.textViewColorName)
        listItemTextColorName.text = list[position].colorName

        updatItemTextAmount(view, position)

        val listItemImage = view.findViewById<ImageView>(R.id.imageViewPart)
        listItemImage.setImageBitmap(list[position].image)

        //Handle buttons and add onClickListeners
        val plusButton = view.findViewById<Button>(R.id.buttonPlus)
        plusButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(list[position].quantityInStore < list[position].quantityInSet){
                    list[position].quantityInStore += 1
                    dbHandler.updatePart(list[position])
                    updatItemTextAmount(view, position)
                    notifyDataSetChanged()
                }
            }
        })

        val minusButton = view.findViewById<Button>(R.id.buttonMinus)
        minusButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (list[position].quantityInStore >= 1) {
                    list[position].quantityInStore -= 1
                    dbHandler.updatePart(list[position])
                    updatItemTextAmount(view, position)
                    notifyDataSetChanged()
                }
            }
        })

        updatItemTextAmount(view, position)
        return view;
    }

    private fun updatItemTextAmount(view: View?, position: Int) {
        val listItemTextAmount = view!!.findViewById<TextView>(R.id.textViewAmount)
        listItemTextAmount.text = list[position].quantityInStore.toString() + "/" + list[position].quantityInSet.toString()
        val r = view.findViewById<ConstraintLayout>(R.id.myLayout)
        if (list[position].quantityInStore == list[position].quantityInSet) {
            val colorInt = android.graphics.Color.rgb(170, 222, 122)
            r.setBackgroundColor(colorInt)
        } else {
            r.setBackgroundColor(0xFFFFFF)
        }
        list.sortBy{it.quantityInStore - it.quantityInSet}

    }


}