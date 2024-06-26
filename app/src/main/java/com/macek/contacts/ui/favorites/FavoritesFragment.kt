package com.macek.contacts.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FavoritesScreen(
                    onShowContactDetail = {
                        findNavController().navigate(
                            FavoritesFragmentDirections.actionFavoritesFragmentToAddOrEditContactFragment(it)
                        )
                    }
                )
            }
        }
    }
}