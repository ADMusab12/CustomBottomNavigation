package com.codetech.wavybottomnav

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codetech.wavybottomnav.databinding.ActivityMainBinding
import com.tauheed.wavybottomnavigation.WavyBottomNavigation
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem

class MainActivity : AppCompatActivity() {
    private val binding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    private val home = 1
    private val explore = 2
    private val overview = 3
    private val profile = 4
    private val settings = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //wavy
        wavyBottomNavigation()
        //fluid
        fluidBottomNavigation()
    }

    private fun fluidBottomNavigation(){
        //menu items
        binding.fluidBottomNavigation.items = listOf(
            FluidBottomNavigationItem(getString(R.string.home),ContextCompat.getDrawable(this,R.drawable.home)),
            FluidBottomNavigationItem(getString(R.string.explore),ContextCompat.getDrawable(this,R.drawable.explore)),
            FluidBottomNavigationItem(getString(R.string.overview),ContextCompat.getDrawable(this,R.drawable.overview)),
            FluidBottomNavigationItem(getString(R.string.profile),ContextCompat.getDrawable(this,R.drawable.profile)),
            FluidBottomNavigationItem(getString(R.string.settings),ContextCompat.getDrawable(this,R.drawable.settings)),
        )

        //apply different colors
        binding.fluidBottomNavigation.apply {
            accentColor = ContextCompat.getColor(this@MainActivity, com.tenclouds.fluidbottomnavigation.R.color.accentColor)
            backColor = ContextCompat.getColor(this@MainActivity, com.tenclouds.fluidbottomnavigation.R.color.backColor)
            textColor = ContextCompat.getColor(this@MainActivity, com.tenclouds.fluidbottomnavigation.R.color.textColor)
            iconColor = ContextCompat.getColor(this@MainActivity, com.tenclouds.fluidbottomnavigation.R.color.iconColor)
            iconSelectedColor = ContextCompat.getColor(this@MainActivity, com.tenclouds.fluidbottomnavigation.R.color.iconSelectedColor)
        }

        //apply background colors on menu id
        binding.fluidBottomNavigation.setOnClickListener {
            when(it.id){
                home ->  binding.fluidLayout.setBackgroundColor(Color.parseColor("#D84879"))
                explore ->  binding.fluidLayout.setBackgroundColor(Color.parseColor("#4CAF50"))
                overview ->  binding.fluidLayout.setBackgroundColor(Color.parseColor("#ffa500"))
                profile ->  binding.fluidLayout.setBackgroundColor(Color.parseColor("#ff69b4"))
                settings ->  binding.fluidLayout.setBackgroundColor(Color.parseColor("#6495ed"))
            }
        }
    }

    private fun wavyBottomNavigation(){
        binding.bottomNav.apply {
            //add menu models
            add(WavyBottomNavigation.Model(home,R.drawable.home))
            add(WavyBottomNavigation.Model(explore,R.drawable.explore))
            add(WavyBottomNavigation.Model(overview,R.drawable.overview))
            add(WavyBottomNavigation.Model(profile,R.drawable.profile))
            add(WavyBottomNavigation.Model(settings,R.drawable.settings))

            //show listener
            setOnShowListener {
                when(it.id){
                    home ->  binding.wavyLayout.setBackgroundColor(Color.parseColor("#D84879"))
                    explore ->  binding.wavyLayout.setBackgroundColor(Color.parseColor("#4CAF50"))
                    overview ->  binding.wavyLayout.setBackgroundColor(Color.parseColor("#ffa500"))
                    profile ->  binding.wavyLayout.setBackgroundColor(Color.parseColor("#ff69b4"))
                    settings ->  binding.wavyLayout.setBackgroundColor(Color.parseColor("#6495ed"))
                }
            }

            //menu listener
            setOnClickMenuListener {
                when(it.id){
                    home -> getString(R.string.home)
                    explore -> getString(R.string.explore)
                    overview -> getString(R.string.overview)
                    profile -> getString(R.string.profile)
                    settings -> getString(R.string.settings)
                    else -> getString(R.string.hyphen)
                }
            }

            //count
            setCount(explore,"24")

            //show
            show(home)
        }
    }
}