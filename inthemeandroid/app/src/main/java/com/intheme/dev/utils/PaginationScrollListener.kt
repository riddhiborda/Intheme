package com.intheme.dev.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationScrollListener(
    private val layoutManager: RecyclerView.LayoutManager,
    private val loadMoreItems: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val isScrollingDownOrRight = when (layoutManager) {
            is LinearLayoutManager -> {
                if (layoutManager.orientation == RecyclerView.HORIZONTAL) dx > 0 else dy > 0
            }
            is GridLayoutManager -> {
                if (layoutManager.orientation == RecyclerView.HORIZONTAL) dx > 0 else dy > 0
            }
            else -> false
        }

        if (isScrollingDownOrRight) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                else -> 0
            }

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                loadMoreItems()
            }
        }
    }
}