package com.intheme.dev.ui.adaptor

import android.annotation.SuppressLint
import android.app.Activity
import com.intheme.dev.base.BaseViewAdapter
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.databinding.ItemBranchBinding
import com.intheme.dev.databinding.ItemCustomerListBinding

class CustomerListAdaptor:BaseViewAdapter<CustomerResponse, ItemCustomerListBinding>( { inflater, parent, attach -> ItemCustomerListBinding.inflate(inflater, parent, attach) },
    compareItems = { old, new -> old.id == old.id  },
    compareContents = { old, new -> old == new } // Define how to compare contents
    )  {
    var onEditAndDeleteClick: ((customerResponse: CustomerResponse,  pos: Int,isEdit:Boolean,isView:Boolean,isDelete:Boolean) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBind(binding: ItemCustomerListBinding, item: CustomerResponse, position: Int) {
        binding.also {
            it.tvName.text = item.name
            it.tvEmail.text = item.email
            it.tvMobileNo.text = (item.mobileNo?:0).toString()
            it.tvLocation.text = item.addressLine1
            it.tvBranch.text = item.branchDetails?.branchName?:""
            it.ivDelete.setOnClickListener {
                onEditAndDeleteClick?.invoke(item,position,false,false,true)
            }
            it.ivEdit.setOnClickListener {
                onEditAndDeleteClick?.invoke(item,position,true,false,false)
            }
            it.ivEye.setOnClickListener {
                onEditAndDeleteClick?.invoke(item,position,false,true,false)
            }
        }
    }
}