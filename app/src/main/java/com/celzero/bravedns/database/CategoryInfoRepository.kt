/*
Copyright 2020 RethinkDNS developers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.celzero.bravedns.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.celzero.bravedns.ui.HomeScreenActivity.GlobalVariable.DEBUG
import com.celzero.bravedns.util.Constants.Companion.LOG_TAG

class CategoryInfoRepository  (private val categoryInfoDAO: CategoryInfoDAO){

    fun updateAsync(categoryInfo: CategoryInfo){
        categoryInfoDAO.update(categoryInfo)
    }

   /* fun deleteAsync(categoryInfo : CategoryInfo){
        categoryInfoDAO.delete(categoryInfo)
    }*/

    fun insertAsync(categoryInfo : CategoryInfo){
        categoryInfoDAO.insert(categoryInfo)
    }

    fun getAppCategoryForLiveData(): LiveData<List<CategoryInfo>>{
        return categoryInfoDAO.getAppCategoryListLiveData()
    }

    fun getAppCategoryList(): List<CategoryInfo>{
        return categoryInfoDAO.getAppCategoryList()
    }

    fun updateCategoryInternet(categoryName: String, isInternetBlocked : Boolean){
        categoryInfoDAO.updateCategoryInternet(categoryName, isInternetBlocked)
        if(DEBUG) Log.d(LOG_TAG,"updateCategoryInternet - isInternetBlocked 1 -$isInternetBlocked")
        if(isInternetBlocked){
            categoryInfoDAO.updateBlockedCount(categoryName)
        }else{
            if(DEBUG) Log.d(LOG_TAG,"updateCategoryInternet - isInternetBlocked -$isInternetBlocked")
            categoryInfoDAO.updateBlockedCountToZero(categoryName)
        }
    }

    fun updateNumberOfBlocked(categoryName: String, isAppBlocked: Boolean){
        if(isAppBlocked) {
            val count = categoryInfoDAO.increaseNumberOfBlocked(categoryName)
            val categoryDetail = categoryInfoDAO.getCategoryDetail(categoryName)
            if(categoryDetail.numOfAppsBlocked == categoryDetail.numberOFApps) {
                categoryInfoDAO.updateCategoryInternet(categoryName, true)
            }else{
                categoryInfoDAO.updateCategoryInternet(categoryName, false)
            }
            if(DEBUG) Log.d(LOG_TAG, "Updated value: $count")
        }else {
            val count = categoryInfoDAO.decreaseNumberOfBlocked(categoryName)
            val categoryDetail = categoryInfoDAO.getCategoryDetail(categoryName)
            if (categoryDetail.numOfAppsBlocked == categoryDetail.numberOFApps) {
                categoryInfoDAO.updateCategoryInternet(categoryName, true)
            } else {
                categoryInfoDAO.updateCategoryInternet(categoryName, false)
            }
            if(DEBUG) Log.d(LOG_TAG, "Updated value: $count")
        }
    }

    fun updateBlockedCount(categoryName: String, blockedCount : Int){
        categoryInfoDAO.updateBlockedCount(categoryName, blockedCount)
        val categoryDetail = categoryInfoDAO.getCategoryDetail(categoryName)
        if (categoryDetail.numOfAppsBlocked == categoryDetail.numberOFApps) {
            categoryInfoDAO.updateCategoryInternet(categoryName, true)
        } else {
            categoryInfoDAO.updateCategoryInternet(categoryName, false)
        }
    }

    fun updateWhitelistCountForAll(checked : Boolean){
        if(checked){
            categoryInfoDAO.updateWhitelistCountForAll()
        }else{
            categoryInfoDAO.clearWhitelistCountForAll()
        }

    }

    fun updateWhitelistForCategory(categoryName : String, checked: Boolean){
        if(checked){
            categoryInfoDAO.updateWhitelistForCategory(categoryName)
        }else{
            categoryInfoDAO.clearWhitelistForCategory(categoryName)
        }

    }

    fun updateWhitelistCount(categoryName: String, whitelistCount : Int){
        categoryInfoDAO.updateWhitelistCount(categoryName, whitelistCount)
    }


    fun updateExcludedCount(categoryName: String, excludedCount : Int){
        categoryInfoDAO.updateExcludedCount(categoryName, excludedCount)
    }

    fun updateExcludedCountForAllApp(checked: Boolean){
        if(checked){
            categoryInfoDAO.updateExcludedCountForAllApp()
        }else{
            categoryInfoDAO.clearExcludedCountForAllApp()
        }

    }

    fun updateExcludedCountForCategory(categoryName: String, checked: Boolean){
        if(checked){
            categoryInfoDAO.updateExcludedCountForCategory(categoryName)
        }else{
            categoryInfoDAO.clearExcludedCountForCategory(categoryName)
        }

    }
}