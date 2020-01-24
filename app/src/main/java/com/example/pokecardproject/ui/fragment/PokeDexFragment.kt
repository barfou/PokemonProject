package com.example.pokecardproject.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokecardproject.R
import com.example.pokecardproject.data.model.PokemonBase
import com.example.pokecardproject.listener.ItemClickListener
import com.example.pokecardproject.ui.activity.MainActivity
import com.example.pokecardproject.ui.adapter.PokemonAdapter
import com.example.pokecardproject.ui.widget.holder.OnPokemonClickListener
import kotlinx.android.synthetic.main.fragment_pokedex.*

private const val ARG_VALUE_FIRST = "value"

class PokeDexFragment : Fragment(), OnPokemonClickListener {

    private var valueToDisplay: String? = null
    private var listener: OnPokeDexFragmentInteractionListener? = null

    var mAdapter: PokemonAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(valueToDisplay: String) =
            PokeDexFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VALUE_FIRST, valueToDisplay)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            valueToDisplay = it.getString(ARG_VALUE_FIRST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pokedex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.context != null) {

            mAdapter = PokemonAdapter(
                this as ItemClickListener,
                MainActivity.listPokemons
            )

            if (MainActivity.listPokemons.count() == 0) {

                apiService.GetListPokemons()
            } else {
                mAdapter!!.submitList(MainActivity.listPokemons)
            }

            myRecyclerView.apply {
                layoutManager = GridLayoutManager(this.context, 2)
                adapter = mAdapter
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPokeDexFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnPokeDexFragmentInteractionListener {
        fun onPokeDexFragmentInteraction(url: String)
    }

    override fun invoke(view: View, pokemon: PokemonBase) {
        listener!!.onPokeDexFragmentInteraction(pokemon.url.toString())
    }

    /*override fun onListPokemonLoaded() {
        mAdapter!!.submitList(MainActivity.listPokemons)
    }*/
}