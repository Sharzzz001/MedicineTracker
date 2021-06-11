package com.example.medicinetracker.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinetracker.DataModels.alarmsModel
import com.example.medicinetracker.R
import kotlinx.android.synthetic.main.alarmlistlayout.view.*

class alarmListAdapter(var alarmListItem: List<alarmsModel>, val clickListener: (alarmsModel) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class  imageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(alarmsModel: alarmsModel, clickListener: (alarmsModel) -> Unit){
            //var arrayAdapter = Adapter(this, android.R.layout.simple_list_item_1, alarmsModel)
            itemView.medTitle.text = alarmsModel.MedName
            itemView.doseTitle.text = alarmsModel.type
            itemView.alarmTitle.text= ""
            for (i in 0 until alarmsModel.alarmtext.size){
                itemView.alarmTitle.append(alarmsModel.alarmtext[i])
                itemView.alarmTitle.append("\n")
            }

            itemView.setOnClickListener {
                clickListener(alarmsModel)
            }

            //itemView.listTitle.adapter=arrayAdapter
            //itemView.doseTitle.text = alarmsModel.dose.toString()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarmlistlayout,parent, false)
        return imageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  alarmListItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as imageViewHolder).bind(alarmListItem[position], clickListener)
    }
}