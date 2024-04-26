package com.parental.control.panjacreation.kiddo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.parental.control.panjacreation.kiddo.activities.AppInfo
import com.parental.control.panjacreation.kiddo.databinding.AppsListItemBinding
import com.parental.control.panjacreation.kiddo.util.SharedPreferencesHelper

class AppListRecyclerAdapter(private val context: Context,private val appInfoList: List<AppInfo>): RecyclerView.Adapter<AppListRecyclerAdapter.MyViewHolder>() {
    private val restrictedPackageSet by lazy { SharedPreferencesHelper.getHashSet(context) }
    inner class MyViewHolder(private val binding: AppsListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: AppInfo){
            binding.icon.setImageDrawable(appInfo.icon)
            binding.name.text = appInfo.name
            binding.switchToggle.isChecked = restrictedPackageSet.contains(appInfo.packageName)

            binding.switchToggle.setOnClickListener {
                if (!restrictedPackageSet.contains(appInfo.packageName))
                    restrictedPackageSet.add(appInfo.packageName)
                else restrictedPackageSet.remove(appInfo.packageName)
                SharedPreferencesHelper.saveHashSet(binding.root.context, restrictedPackageSet)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(AppsListItemBinding.inflate(LayoutInflater.from(parent.context) , parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(appInfoList[position])
    }

    override fun getItemCount(): Int {
        return appInfoList.size
    }

}