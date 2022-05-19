package kr.co.nyangtographer.android.nyangtographer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class StageRecordAdapter internal constructor(context : Context, var onDeleteListener : MainViewModel) : RecyclerView.Adapter<StageRecordAdapter.viewHolder>() {

    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var dataSet = emptyList<Record>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = inflater.inflate(R.layout.item_recycler_stage_record,parent,false)
        return viewHolder(view)
    }

    inner class viewHolder(view : View): RecyclerView.ViewHolder(view) {
        val tvTitle : TextView = view.findViewById(R.id.tv_title)
        val tvRecordScore : TextView = view.findViewById(R.id.tv_record_score)
        val ivImg : ImageView = view.findViewById(R.id.iv_quest_img)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val record = dataSet[position]
        holder.tvTitle.text = record.title
        var pass = "실패"
        if (record.pass) pass = "성공"
        holder.tvRecordScore.text = if (record.similarity == 0) "이전 기록 : 없음" else "이전 기록 : $pass(${record.similarity})"
        holder.ivImg.setImageBitmap(record.quest_img)

        // (1) 리스트 내 항목 클릭 시 onClick() 호출
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    internal fun setDataSet(recordList: List<Record>) {
        this.dataSet = recordList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataSet.size

}