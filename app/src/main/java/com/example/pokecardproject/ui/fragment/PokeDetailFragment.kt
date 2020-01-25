package com.example.pokecardproject.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pokecardproject.*
import com.example.pokecardproject.ui.adapter.AbilityAdapter
import com.example.pokecardproject.data.model.PokemonInfo
import com.example.pokecardproject.ui.viewmodel.DetailPokemonViewModel
import com.example.pokecardproject.utils.Utils
import kotlinx.android.synthetic.main.fragment_pokedetail.*

private const val URL = "Url"

class PokeDetailFragment : Fragment() {

    private lateinit var detailPokemonViewModel: DetailPokemonViewModel
    private lateinit var url: String
    private var listener: OnPokeDetailFragmentInteractionListener? = null
    var mAdapter: AbilityAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            PokeDetailFragment().apply {
                arguments = Bundle().apply {
                    //putString(POKEMON_INFO, Gson().toJson(pokemonInfo))
                    putString(URL, url)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailPokemonViewModel = ViewModelProvider(this, DetailPokemonViewModel).get()
            url = it.getString(URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pokedetail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (url != "") {
            detailPokemonViewModel.getPokemonDetails(url) {
                if (it != null) {
                    showInfos(it)
                } else {
                    showAlert()
                }
            }
        }

        mAdapter = AbilityAdapter(this)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPokeDetailFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Erreur")
        builder.setMessage(R.string.download_failed)
        builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
        builder.show()
    }

    private fun showInfos(pokemonInfo: PokemonInfo) {

        if (pokemonInfo != null) {

            Glide.with(image_view.context)
                .load(pokemonInfo.png_Urls?.front_default)
                .into(image_view)

            adjustSeekBars(pokemonInfo)

            tv_nom.text = pokemonInfo!!.name.toString()
            tv_poids.text = pokemonInfo!!.weight.toString()
            tv_taille.text = pokemonInfo!!.height.toString()

            if (this.context != null && pokemonInfo.listeAbilty != null) {
                mAdapter?.submitList(pokemonInfo.listeAbilty!!)
            }
        }
    }

    private fun adjustSeekBars(pokemonInfo: PokemonInfo?) {

        var attaque: Int? = pokemonInfo!!.getStat(PokemonInfo.StatType.ATTAQUE)?.base_stat
        if (attaque != null) {
            seek_bar_attack.setProgress(attaque)
            lbl_attaque.append(" : ")
            lbl_attaque.append(attaque.toString())
        }

        var defense: Int? = pokemonInfo!!.getStat(PokemonInfo.StatType.DEFENSE)?.base_stat
        if (defense != null) {
            seek_bar_defense.setProgress(defense)
            lbl_defense.append(" : ")
            lbl_defense.append(defense.toString())
        }

        var attaque_speciale: Int? =
            pokemonInfo!!.getStat(PokemonInfo.StatType.ATTAQUE_SPECIALE)?.base_stat
        if (attaque_speciale != null) {
            seek_bar_special_attack.setProgress(attaque_speciale)
            lbl_special_attaque.append(" : ")
            lbl_special_attaque.append(attaque_speciale.toString())
        }

        var defense_speciale: Int? =
            pokemonInfo!!.getStat(PokemonInfo.StatType.DEFENSE_SPECIALE)?.base_stat
        if (defense_speciale != null) {
            seek_bar_special_defense.setProgress(defense_speciale)
            lbl_special_defense.append(" : ")
            lbl_special_defense.append(defense_speciale.toString())
        }

        Utils.lockSeekBar(seek_bar_attack)
        Utils.lockSeekBar(seek_bar_defense)
        Utils.lockSeekBar(seek_bar_special_attack)
        Utils.lockSeekBar(seek_bar_special_defense)
    }

    interface OnPokeDetailFragmentInteractionListener {
        fun onPokeDetailFragmentInteraction()
    }
}