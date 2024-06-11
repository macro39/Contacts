package com.macek.contacts.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.macek.contacts.ui.addoreditcontact.INVALID_CONTACT_ID_ARG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ContactsScreen(
                    onAddContact = {
                        findNavController().navigate(
                            ContactsFragmentDirections.actionContactsFragmentToAddOrEditContactFragment(INVALID_CONTACT_ID_ARG)
                        )
                    },
                    onShowContactDetail = {
                        findNavController().navigate(
                            ContactsFragmentDirections.actionContactsFragmentToAddOrEditContactFragment(it)
                        )
                    }
                )
            }
        }
    }
}