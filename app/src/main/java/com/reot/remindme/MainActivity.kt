package com.reot.remindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.reot.remindme.Adapters.ViewPagerAdapter.ViewPagerAdapter
import com.reot.remindme.Fragments.Pages.ArchiveFragment
import com.reot.remindme.Fragments.Pages.AboutFragment
import com.reot.remindme.Fragments.Pages.TaskFragment


class MainActivity : AppCompatActivity() {
    private lateinit var viewPager : ViewPager
    private lateinit var tabs : TabLayout
    lateinit var adapter:ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        tabs= findViewById(R.id.tabs)
        setUpTabs()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {}
                    1 -> {
                        adapter.getItem(1).onResume()
                    }
                    2 -> {}
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setUpTabs() {
        adapter= ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(TaskFragment(),"Tasks")
        adapter.addFragment(ArchiveFragment(),"Archive")
        adapter.addFragment(AboutFragment(),"Settings")
        viewPager.adapter=adapter
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.baseline_content_paste_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.baseline_archive_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.baseline_settings_24)
    }
}