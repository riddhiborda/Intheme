package com.intheme.dev.base

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseViewAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val compareItems: (T, T) -> Boolean,
    private val compareContents: (T, T) -> Boolean
) : RecyclerView.Adapter<BaseViewAdapter<T, VB>.ViewHolder>() {

    var itemList = ArrayList<T>()
    private var isPagingLoading: Boolean = false
    private var isInitLoading: Boolean = false
    private var intiSize: Int = 0

    abstract fun onBind(binding: VB, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: VB = bindingInflater(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setPaginationLoading(isLoading: Boolean,loadingItem:T?= null) {
        val oldList = ArrayList(itemList)
        if(isLoading){
            loadingItem?.let {itemList.add(it) }
        }else{
            itemList.removeAt(itemList.lastIndex)
        }
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

    fun setInitLoadingItem(isInitLoading: Boolean,loadingItemList:ArrayList<T> = ArrayList()) {
        val oldList = ArrayList(itemList)
        if(isInitLoading){
            itemList = loadingItemList
        }else{
            itemList.clear()
        }
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

    fun getPagingLoading() = this.isPagingLoading

    fun getInitLoading() = this.isInitLoading

    override fun getItemCount(): Int = itemList.size

    fun getList(): ArrayList<T> {
        return itemList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = itemList[position]
            holder.bind(item, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, position: Int) {
            onBind(binding, item, position)
        }
    }

    fun updateItem(item: T, position: Int,) {
        itemList[position] = item
        try{
            Handler(Looper.getMainLooper()).post {
                notifyItemChanged(position)
            }
        }catch (_:Exception){
        }

    }



    fun updateList(newItemList: List<T>) {
        itemList.clear()
        itemList.addAll(newItemList)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(itemList, newItemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(newItems: List<T>) {
        val oldList = ArrayList(itemList)
        itemList.addAll(newItems)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceArrayList(newItemList: List<T>) {
        val data = BaseDiffUtilCallback(itemList, newItemList, compareItems, compareContents)
        val diffResult = DiffUtil.calculateDiff(data)
        diffResult.dispatchUpdatesTo(this)
        itemList.clear()
        itemList.addAll(newItemList) // Replace the old list with the new list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        val oldList = ArrayList(itemList)
        itemList.removeAt(position)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }


    fun addItem(newItem: T) {
        val oldList = ArrayList(itemList)
        itemList.add(newItem)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearArrayList() {
        val oldList = ArrayList(itemList) // Create a copy of the current list
        itemList.clear()
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }
}
//How to Use Adaptor
// Initialize the adapter
//adapter = object : BaseViewAdapter<Item, ItemViewBinding>(
//    { inflater, parent, attach -> ItemViewBinding.inflate(inflater, parent, attach) },
//    compareItems = { old, new -> old.id == new.id },  // Define how to compare items
//    compareContents = { old, new -> old == new } // Define how to compare contents
//) {
//    override fun onBind(binding: ItemViewBinding, item: Item, position: Int) {
//        binding.textViewName.text = item.name // Bind data to the view
//    }
//}
//
//// Set the adapter
//recyclerView.adapter = adapter