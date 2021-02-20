package com.kekadoc.test.companies.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kekadoc.test.companies.MainActivity
import com.kekadoc.test.companies.model.Company
import com.kekadoc.test.companies.R

class DashboardFragment : Fragment() {

    companion object {
        private const val KEY_COMPANY_ID = "CompanyID"

        fun createBundle(companyId: Long?): Bundle {
            return bundleOf(KEY_COMPANY_ID to companyId)
        }
        private fun companyIdFromBundle(bundle: Bundle): Long {
            return bundle.getLong(KEY_COMPANY_ID)
        }
    }

    private var viewModel: DashboardFragmentModel? = null

    private lateinit var companyView: View
    private lateinit var messageDataErrorView: View
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var imageViewCompanyIcon: ImageView
    private lateinit var textViewCompanyId: TextView
    private lateinit var textViewCompanyName: TextView
    private lateinit var textViewCompanyDescription: TextView
    private lateinit var textViewCompanyLocation: TextView
    private lateinit var textViewCompanyWeb: TextView
    private lateinit var textViewCompanyPhone: TextView

    private var companyId: Long = 0
    set(value) {
        field = value
        refreshCompany()
    }

    private fun refreshCompanyUI(company: Company?) {
        if (company == null) {
            companyView.visibility = View.GONE
            messageDataErrorView.visibility = View.VISIBLE
        } else {
            companyView.visibility = View.VISIBLE
            messageDataErrorView.visibility = View.GONE

            imageViewCompanyIcon.load(company.imageUrl) {
                crossfade(false)
                placeholder(R.drawable.ic_baseline_cloud_download_24)
                error(R.drawable.ic_baseline_error_24)
            }
            val id = "ID: ${company.id}"
            textViewCompanyId.text = id
            textViewCompanyName.text = company.name
            textViewCompanyDescription.text = company.description.toString()

            textViewCompanyLocation.apply {
                if (company.lat == null || company.lon == null || company.lat == 0.0 || company.lon == 0.0) {
                    visibility = View.GONE
                } else{
                    val location = "[${company.lat}, ${company.lon}]"
                    text = location
                    visibility = View.VISIBLE
                }
            }
            textViewCompanyWeb.apply {
                text = company.www
                visibility = if (text == null || text.isEmpty()) View.GONE
                else View.VISIBLE
            }
            textViewCompanyPhone.apply {
                text = company.phone
                visibility = if (text == null || text.isEmpty()) View.GONE
                else View.VISIBLE
            }
        }
    }

    private fun refreshCompany() {
        viewModel?.loadCompany(companyId)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        progressIndicator = root.findViewById(R.id.progressIndicator)
        companyView = root.findViewById(R.id.companyView)
        messageDataErrorView = root.findViewById(R.id.messageDataError)
        imageViewCompanyIcon = root.findViewById(R.id.imageView_companyIcon)
        textViewCompanyId = root.findViewById(R.id.textView_companyId)
        textViewCompanyName = root.findViewById(R.id.textView_companyName)
        textViewCompanyDescription = root.findViewById(R.id.textView_companyDesc)
        textViewCompanyLocation = root.findViewById(R.id.textView_companyLocation)
        textViewCompanyWeb = root.findViewById(R.id.textView_companyWeb)
        textViewCompanyPhone = root.findViewById(R.id.textView_companyPhone)

        viewModel = ViewModelProvider(this).get(DashboardFragmentModel::class.java)
        viewModel?.let {
            it.getCompany().observe(viewLifecycleOwner, { company ->
                refreshCompanyUI(company)
            })
            it.getLoadingProcess().observe(viewLifecycleOwner, {
                if (it) {
                    progressIndicator.show()
                    messageDataErrorView.visibility = View.GONE
                } else {
                    progressIndicator.hide()
                }
            })
        }

        val arg = arguments
        arg?.let {
            companyId = companyIdFromBundle(it)
        }

        val activity = requireActivity() as MainActivity
        activity.apply {
            onRefreshAction = {
                refreshCompany()
            }
        }

        return root
    }

}