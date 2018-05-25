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


/**
 * Created by Oliwia on 25.05.2018.
 */
class MyAdapter(list: ArrayList<String>, private val context: Context) : BaseAdapter(), ListAdapter {
    private var list = ArrayList<String>(listOf("a", "bb", "ccc"))


    /*init {
        list.add("Raz")
        list.add("Dwa2")
        list.add("elo32")
    }*/

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(pos: Int): String {
        return list[pos]
    }

    override fun getItemId(pos: Int): Long {
        return list[pos].length.toLong()
        //just return 0 if your list items do not have an Id variable.
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.my_row, null)
        }

        //Handle TextView and display string from your list
        val listItemText = view!!.findViewById<TextView>(R.id.textViewName)
        listItemText.text = list[position]

        //Handle buttons and add onClickListeners
        val deleteBtn = view!!.findViewById<Button>(R.id.buttonDelete)

        deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //do something
                list.removeAt(position) //or some other task
                notifyDataSetChanged()
            }
        })
        return view
    }
}