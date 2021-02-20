package com.kekadoc.test.companies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import coil.load
import com.kekadoc.test.companies.MainActivity
import com.kekadoc.test.companies.model.Company
import com.kekadoc.test.companies.R

class DashboardFragment : Fragment() {

    companion object {

        private const val KEY_IMAGE_URL = "CompanyImageUrl"
        private const val KEY_ID = "CompanyID"
        private const val KEY_NAME = "CompanyName"

        fun createBundle(company: Company?): Bundle {
            val imageUrl = company?.imageUrl
            val id = company?.id ?: 0
            val name = company?.name ?: "null"
            return bundleOf(
                KEY_IMAGE_URL to imageUrl,
                KEY_ID to id,
                KEY_NAME to name,
            )
        }

        private fun fromBundle(bundle: Bundle): Company {
            return Company(
                bundle.getLong(KEY_ID),
                bundle.getString(KEY_NAME) ?: "null",
                bundle.getString(KEY_IMAGE_URL)
            )
        }

    }

    private lateinit var imageViewCompanyIcon: ImageView
    private lateinit var textViewCompanyId: TextView
    private lateinit var textViewCompanyName: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        imageViewCompanyIcon = root.findViewById(R.id.imageView_companyIcon)
        textViewCompanyId = root.findViewById(R.id.textView_companyId)
        textViewCompanyName = root.findViewById(R.id.textView_companyName)

        val arg = arguments
        arg?.let {
            val company = fromBundle(it)
            imageViewCompanyIcon.load(company.imageUrl) {
                crossfade(true)
                crossfade(50)
                placeholder(R.drawable.ic_baseline_cloud_download_24)
                error(R.drawable.ic_baseline_error_24)
            }
            val id = "id: ${company.id}"
            textViewCompanyId.text = id

            textViewCompanyName.text = company.name

            (requireActivity() as MainActivity).setAccessRefreshAction(false)
        }

        return root
    }

}