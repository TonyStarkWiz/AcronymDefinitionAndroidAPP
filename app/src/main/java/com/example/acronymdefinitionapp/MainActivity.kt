package com.example.acronymdefinitionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.acronymdefinitionapp.adapter.AcronymDefAdapter
import com.example.acronymdefinitionapp.databinding.ActivityMainBinding
import com.example.acronymdefinitionapp.model.Definition
import com.example.acronymdefinitionapp.model.Lf
import com.example.acronymdefinitionapp.utils.RequestState
import com.example.acronymdefinitionapp.viewmodel.AcronymViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[AcronymViewModel::class.java]
    }

    private val acronymAdapter by lazy {
        AcronymDefAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.acronymViewModel = this@MainActivity.viewModel
        binding.lifecycleOwner = this@MainActivity
        binding.acronymAdapter = this@MainActivity.acronymAdapter

        setContentView(binding.root)

        binding.searchAcronym.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    viewModel.handleSearch(it)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }
}

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: AcronymDefAdapter?
) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

@BindingAdapter("submitList")
fun submitList(recyclerView: RecyclerView, state: RequestState?) {
    val adapter = recyclerView.adapter as? AcronymDefAdapter

    when (state) {
        is RequestState.SUCCESS<*> -> {
            val definitions = (state.definitions as List<Definition>).firstOrNull()?.lfs ?: listOf()
            adapter?.setDataDefinition(definitions)
        }
        is RequestState.ERROR -> {
            AlertDialog.Builder(recyclerView.context)
                .setTitle("Error has occurred")
                .setMessage(state.exception.localizedMessage)
                .setNegativeButton("DISMISS") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
            recyclerView.visibility = View.GONE
        }
        is RequestState.LOADING -> {
            recyclerView.visibility = View.GONE
        }
        else -> {
            // no-op
        }
    }
}

@BindingAdapter("setProgressLoad")
fun setProgressLoad(bar: ProgressBar, isLoading: Boolean) {
    if (!isLoading) {
        bar.visibility = View.GONE
    } else {
        bar.visibility = View.VISIBLE
    }
}