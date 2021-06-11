package com.example.medicinetracker.RecyclerViewAdapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinetracker.DataModels.PatinetalarmModel
import com.example.medicinetracker.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.alarmlistlayout.view.*



class patientListAdapter(var ptalarmListItem: List<PatinetalarmModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var arrayAdapter: ArrayAdapter<String>
    class  imageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(PatinetalarmModel: PatinetalarmModel){
            //var arrayAdapter = Adapter(this, android.R.layout.simple_list_item_1, alarmsModel)
            itemView.medTitle.text = PatinetalarmModel.MedName
            itemView.doseTitle.text = PatinetalarmModel.type
            itemView.alarmTitle.text= ""
            for (i in 0 until PatinetalarmModel.alarmtext.size){
                itemView.alarmTitle.append(PatinetalarmModel.alarmtext[i])
                itemView.alarmTitle.append("\n")

            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarmlistlayout,parent, false)
        return imageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as imageViewHolder).bind(ptalarmListItem[position],)
    }

    override fun getItemCount(): Int {
        return  ptalarmListItem.size
    }
}