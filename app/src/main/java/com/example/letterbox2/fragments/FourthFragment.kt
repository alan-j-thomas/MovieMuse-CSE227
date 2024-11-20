package com.example.letterbox2.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.letterbox2.R
import com.example.letterbox2.ShowTheatresActivity

class FourthFragment : Fragment() {

    private lateinit var btnopen1: Button
    private lateinit var btnopen2: Button
    private lateinit var btnopen3: Button
    private lateinit var loc1: TextView
    private lateinit var loc2: TextView
    private lateinit var loc3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fourth, container, false)

        // Initialize views
        loc1 = view.findViewById(R.id.location_text)
        loc2 = view.findViewById(R.id.location_text2)
        loc3 = view.findViewById(R.id.location_text3)

        btnopen1 = view.findViewById(R.id.btnopen1)
        btnopen2 = view.findViewById(R.id.btnopen2)
        btnopen3 = view.findViewById(R.id.btnopen3)


        btnopen1.setOnClickListener {
            val loc = loc1.text.toString() + " Kanakapura Road, Bangalore"
            Intent(requireContext(), ShowTheatresActivity::class.java).also {
                it.putExtra("location", loc)
                startActivity(it)
            }
        }

        btnopen2.setOnClickListener {
            val loc = loc2.text.toString() + " Jalandhar"
            Intent(requireContext(), ShowTheatresActivity::class.java).also {
                it.putExtra("location", loc)
                startActivity(it)
            }
        }

        btnopen3.setOnClickListener {
            val loc = loc3.text.toString() + " Srinagar"
            Intent(requireContext(), ShowTheatresActivity::class.java).also {
                it.putExtra("location", loc)
                startActivity(it)
            }
        }

        return view
    }
}
