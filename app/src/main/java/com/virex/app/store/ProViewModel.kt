package com.virex.app.store

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virex.billing.BillingGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProViewModel @Inject constructor(
    private val billingGateway: BillingGateway
) : ViewModel() {

    val isProActive: StateFlow<Boolean> = billingGateway.isProActive

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Purchase PRO subscription
     */
    fun purchasePro(activity: Activity, isYearly: Boolean) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            // Uncomment when BillingGateway is fully implemented
            /*
            billingGateway.launchProPurchase(
                activity = activity,
                isYearly = isYearly,
                onSuccess = {
                    _isLoading.value = false
                    // Purchase successful - isProActive will update automatically
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _errorMessage.value = error
                }
            )
            */
            
            // Temporary mock for testing
            kotlinx.coroutines.delay(2000)
            _isLoading.value = false
            _errorMessage.value = "Billing not yet configured. Add Google Play Billing dependency."
        }
    }

    /**
     * Restore previous purchases
     */
    fun restorePurchases() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            // Uncomment when BillingGateway is fully implemented
            /*
            val restored = billingGateway.restorePurchases()
            _isLoading.value = false
            if (restored) {
                // Success - isProActive will update
            } else {
                _errorMessage.value = "No previous purchases found"
            }
            */
            
            // Temporary mock
            kotlinx.coroutines.delay(1500)
            _isLoading.value = false
            _errorMessage.value = "No purchases to restore (billing not configured)"
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
