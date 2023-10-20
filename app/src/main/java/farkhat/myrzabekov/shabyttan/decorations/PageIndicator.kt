package farkhat.myrzabekov.shabyttan.decorations

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PageIndicator(
    private val layoutManager: LinearLayoutManager,
    private val indicators: List<CardView>
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val position = layoutManager.findFirstVisibleItemPosition()
        Log.d("MyTag", position.toString())
        indicators.forEachIndexed { index, cardView ->
            if (index == position) {
                cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
            } else {
                cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6A6A6A"))
            }
        }

    }
}
