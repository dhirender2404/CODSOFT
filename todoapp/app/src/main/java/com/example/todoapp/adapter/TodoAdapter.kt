package com.example.todoapp.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.TodoLayoutBinding
import com.example.todoapp.fragments.HomeFragmentDirections
import com.example.todoapp.model.Todo

class TodoAdapter(val listener : DbUpdateListener) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    interface DbUpdateListener{
        fun updateTodo(newItem: Todo)
    }


    class TodoViewHolder(val itemBinding: TodoLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)
    private val differCallback = object : DiffUtil.ItemCallback<Todo>(){

        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.todoDesc == newItem.todoDesc &&
                    oldItem.todoTitle == newItem.todoTitle

        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
    val differ =AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {

        return TodoViewHolder(
            TodoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }




    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = differ.currentList[position]
        holder.itemBinding.todoTitle.text = currentTodo.todoTitle
        holder.itemBinding.todoDesc.text = currentTodo.todoDesc
        holder.itemBinding.txtShowDate.text = currentTodo.date
        holder.itemBinding.txtShowTime.text = currentTodo.time
        holder.itemBinding.statu.text=currentTodo.stat
        holder.itemBinding.pri.text = currentTodo.priori
        val context = holder.itemBinding.root.context

        if(currentTodo.stat == "Completed"){
            holder.itemBinding.todoTitle.isChecked = true
            holder.itemBinding.statu.setTextColor(ContextCompat.getColor(context, R.color.green))
        }

        holder.itemBinding.todoTitle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentTodo.stat  = "Completed"
                holder.itemBinding.statu.setTextColor(ContextCompat.getColor(context, R.color.green))



            } else {
                currentTodo.stat  = "Not completed"
                holder.itemBinding.statu.setTextColor(ContextCompat.getColor(context, R.color.black))

            }
            notifyItemChanged(position)
            listener.updateTodo(currentTodo)
        }


        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditTodoFragment(currentTodo)
            it.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}






