package com.intheme.dev.ui.adaptor

import android.content.Context
import com.intheme.dev.base.BaseViewAdapter
import com.intheme.dev.data.model.response.User
import com.intheme.dev.databinding.ItemBranchAdminBinding
import com.intheme.dev.databinding.ItemServiceManBinding
import com.intheme.dev.utils.load

class ServiceManListAdaptor(val context: Context):BaseViewAdapter<User, ItemServiceManBinding>( { inflater, parent, attach -> ItemServiceManBinding.inflate(inflater, parent, attach) },
    compareItems = { old, new -> old.id == old.id  },
    compareContents = { old, new -> old == new } // Define how to compare contents
    )  {
    var onEditClick: ((userResponse: User,  pos: Int) -> Unit)? = null
    var onDeleteClick: ((userResponse: User,  pos: Int) -> Unit)? = null
    var onActionUpdateClick: ((userResponse: User,  pos: Int) -> Unit)? = null

    override fun onBind(binding: ItemServiceManBinding, item: User, position: Int) {
        binding.also {
            it.tvName.text = item.name
            it.tvEmail.text = item.email
            it.tvMobileNo.text = item.mobileNo
            it.tvBranch.text = item.branchDetails?.branchName?:""
            it.ivUser.load(context,item.image?:"")
            it.ivDelete.setOnClickListener {
                onDeleteClick?.invoke(item,position)
            }
            it.ivEdit.setOnClickListener {
                onEditClick?.invoke(item,position)
            }
            it.swAction.setOnClickListener {
                onActionUpdateClick?.invoke(item,position)
            }
        }
    }
}