package com.intheme.dev.ui.adaptor

import android.annotation.SuppressLint
import android.app.Activity
import com.bumptech.glide.Glide
import com.intheme.dev.base.BaseViewAdapter
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.data.model.response.EquipmentResponse
import com.intheme.dev.databinding.ItemBranchBinding
import com.intheme.dev.databinding.ItemCustomerListBinding
import com.intheme.dev.databinding.ItemEquipmentsBinding

class EquipmentListAdaptor :
    BaseViewAdapter<EquipmentResponse, ItemEquipmentsBinding>({ inflater, parent, attach ->
        ItemEquipmentsBinding.inflate(
            inflater,
            parent,
            attach
        )
    },
        compareItems = { old, new -> old.id == old.id },
        compareContents = { old, new -> old == new } // Define how to compare contents
    ) {

    var onEditAndDeleteClick: ((equipmentResponse: EquipmentResponse, pos: Int, isEdit: Boolean, isView: Boolean, isDelete: Boolean) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBind(binding: ItemEquipmentsBinding, item: EquipmentResponse, position: Int) {
        binding.also {
            if (item.equipmentImages.isNotEmpty())
                Glide.with(binding.root.context).load(item.equipmentImages[0].imageUrl).into(it.ivEquipment)

            it.tvOperatorName.text = item.mechine
            it.tvCustomerName.text = item.customerBy?.name

            it.ivDelete.setOnClickListener {
                onEditAndDeleteClick?.invoke(item, position, false, false, true)
            }
            it.ivEdit.setOnClickListener {
                onEditAndDeleteClick?.invoke(item, position, true, false, false)
            }
            it.ivView.setOnClickListener {
                onEditAndDeleteClick?.invoke(item, position, false, true, false)
            }
        }
    }
}