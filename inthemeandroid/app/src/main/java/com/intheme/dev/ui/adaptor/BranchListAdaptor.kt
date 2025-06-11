package com.intheme.dev.ui.adaptor

import android.app.Activity
import com.intheme.dev.base.BaseViewAdapter
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.databinding.ItemBranchBinding

class BranchListAdaptor:BaseViewAdapter<BranchResponse, ItemBranchBinding>( { inflater, parent, attach -> ItemBranchBinding.inflate(inflater, parent, attach) },
    compareItems = { old, new -> old.id == old.id  },
    compareContents = { old, new -> old == new } // Define how to compare contents
    )  {
    var onEditClick: ((branchResponse: BranchResponse,  pos: Int) -> Unit)? = null
    var onDeleteClick: ((branchResponse: BranchResponse,  pos: Int) -> Unit)? = null

    override fun onBind(binding: ItemBranchBinding, item: BranchResponse, position: Int) {
        binding.also {
            it.tvBranchName.text = item.branchName
            it.tvBranchLocation.text = item.branchLocation
            it.ivDelete.setOnClickListener {
                onDeleteClick?.invoke(item,position)
            }
            it.ivEdit.setOnClickListener {
                onEditClick?.invoke(item,position)
            }
        }
    }
}