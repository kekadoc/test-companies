package com.kekadoc.test.companies.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.Disposable
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kekadoc.test.companies.*
import com.kekadoc.test.companies.model.CompanyPreview
import com.kekadoc.test.companies.ui.dashboard.DashboardFragment

class HomeFragment : Fragment() {

    private var viewModel: HomeFragmentModel? = null
    private var adapter: Adapter? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageNotFoundView: View

    private fun refreshCompaniesUI(companies: List<CompanyPreview>?) {
        adapter?.submitList(companies)
        if (companies == null || companies.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            messageNotFoundView.visibility = View.VISIBLE
        } else{
            recyclerView.visibility = View.VISIBLE
            messageNotFoundView.visibility = View.INVISIBLE
        }
    }
    private fun refreshCompanies() {
        viewModel?.refreshData()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val progressIndicator = root.findViewById<LinearProgressIndicator>(R.id.progressIndicator)
        recyclerView = root.findViewById(R.id.recyclerView)
        messageNotFoundView= root.findViewById(R.id.messageDataError)

        viewModel = ViewModelProvider(requireActivity(), HomeFragmentModelFactory).get(HomeFragmentModel::class.java).apply {
            this.getCompanies().observe(viewLifecycleOwner, {
                refreshCompaniesUI(it)
            })
            getLoadingProcess().observe(viewLifecycleOwner, {
                if (it) {
                    messageNotFoundView.visibility = View.INVISIBLE
                    progressIndicator.show()
                }
                else progressIndicator.hide()
            })
        }

        recyclerView.apply {
            this@HomeFragment.adapter = Adapter()
            adapter = this@HomeFragment.adapter
            val space = Utils.dpToPx(requireContext(), 4f).toInt()
            val rect = Rect(space, space, space, space)
            addItemDecoration(ItemDecorator(rect))
        }

        val activity = requireActivity() as MainActivity
        activity.apply {
            onRefreshAction = {
                refreshCompanies()
            }
        }

        return root
    }

    private open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            private const val TAG: String = "ViewHolder-TAG"
        }
        var company: CompanyPreview? = null
            set(value) {
                val old = field
                field = value
                onCompanyChange(old, field)
            }

        var onChoiceEvent: ((company: CompanyPreview) -> Unit)? = null

        private var imageViewIcon: ImageView = itemView.findViewById(R.id.imageView)
        private var textViewName: TextView = itemView.findViewById(R.id.textView)

        private var coilLoad: Disposable? = null

        init {
            company = null
            itemView.findViewById<CardView>(R.id.cardView).apply {
                setOnClickListener {
                    company?.let { onChoiceEvent?.invoke(it)  }
                }
            }
        }

        protected open fun onCompanyChange(oldItem: CompanyPreview?, newItem: CompanyPreview?) {
            coilLoad?.dispose()
            if (newItem == null) {
                imageViewIcon.setImageResource(getDefaultImageRes())
                textViewName.setText(R.string.company_null_name)
            } else{
                coilLoad = imageViewIcon.load(newItem.imageUrl) {
                    crossfade(true)
                    crossfade(100)
                    placeholder(getTempImageRes())
                    error(getDefaultImageRes())
                }
                val name = newItem.name ?: textViewName.context.getString(R.string.company_null_name)
                textViewName.text = name
            }

        }

        @DrawableRes private fun getDefaultImageRes() = R.drawable.ic_baseline_error_24
        @DrawableRes private fun getTempImageRes() = R.drawable.ic_baseline_cloud_download_24

    }

    private class DiffItemCallback : DiffUtil.ItemCallback<CompanyPreview>() {

        override fun areItemsTheSame(oldItem: CompanyPreview, newItem: CompanyPreview): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: CompanyPreview, newItem: CompanyPreview): Boolean {
            return oldItem == newItem
        }

    }

    private inner class Adapter : ListAdapter<CompanyPreview, ViewHolder>(DiffItemCallback()) {

        private var inflater: LayoutInflater? = null

        private fun getInflater(context: Context): LayoutInflater {
            if (inflater == null) inflater = LayoutInflater.from(context)
            return inflater!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                getInflater(parent.context)
                    .inflate(R.layout.view_company_item, parent, false)
            ).apply {
                onChoiceEvent = {
                    (activity as MainActivity).navigate(
                        R.id.action_navigation_home_to_navigation_dashboard, DashboardFragment.createBundle(it.id)
                    )
                }
            }
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.company = getItem(position)
        }

    }

    private class ItemDecorator(val rect: Rect) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(rect)
        }
    }

}