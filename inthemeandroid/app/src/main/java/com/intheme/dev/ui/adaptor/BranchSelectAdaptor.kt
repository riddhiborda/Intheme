package com.intheme.dev.ui.adaptor


import com.intheme.dev.base.BaseViewAdapter
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.databinding.ItemSelectBranchBinding


class BranchSelectAdaptor: BaseViewAdapter<BranchResponse, ItemSelectBranchBinding>(
    { inflater, parent, attach -> ItemSelectBranchBinding.inflate(inflater, parent, attach) },
    compareItems = { old, new -> old.id.equals(new.id) },  // Define how to compare items
    compareContents = { old, new -> old == new } // Define how to compare contents
) {
    var citySelect :((branchResponse: BranchResponse, pos:Int)->Unit)?=null
    override fun onBind(binding: ItemSelectBranchBinding, item: BranchResponse, position: Int) {
        binding.root.setOnClickListener {
            try{
                citySelect?.invoke(item,position)
            }catch (_:Exception){}
        }

        binding.tvBranchName.text = item.branchName
        binding.tvBranchLocation.text = item.branchLocation

    }
}