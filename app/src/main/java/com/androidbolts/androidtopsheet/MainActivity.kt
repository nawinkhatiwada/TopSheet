package com.androidbolts.androidtopsheet

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.androidbolts.topsheet.TopSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topSheetBehavior = TopSheetBehavior.from(topSheetContainer)
        button = findViewById(R.id.btn_open_top_sheet)

        button.setOnClickListener {
            openTopSheet()
        }
    }

    private fun openTopSheet() {
        topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
        topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }
}
