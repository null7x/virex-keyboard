package com.virex.app.store

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.virex.billing.BillingGateway
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProScreen(
    onBack: () -> Unit,
    viewModel: ProViewModel = hiltViewModel()
) {
    val isPro by viewModel.isProActive.collectAsState(initial = false)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    var selectedPlan by remember { mutableStateOf(PricingPlan.MONTHLY) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050505), Color(0xFF101322), Color(0xFF0C0620))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        if (isPro) {
            // Already PRO
            ProActiveState(onManage = { /* TODO: Open Play Store subscriptions */ })
        } else {
            // Paywall
            ProPaywall(
                selectedPlan = selectedPlan,
                onPlanSelected = { selectedPlan = it },
                isLoading = isLoading,
                errorMessage = errorMessage,
                onPurchase = {
                    val activity = context as? Activity
                    if (activity != null) {
                        viewModel.purchasePro(activity, selectedPlan == PricingPlan.YEARLY)
                    }
                },
                onRestore = { viewModel.restorePurchases() }
            )
        }
    }
}

@Composable
private fun ProActiveState(onManage: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👑",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You're PRO!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enjoy all premium features",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onManage,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Manage Subscription")
        }
    }
}

@Composable
private fun ProPaywall(
    selectedPlan: PricingPlan,
    onPlanSelected: (PricingPlan) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPurchase: () -> Unit,
    onRestore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "⭐",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Virex PRO",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Features
        ProFeaturesList()

        Spacer(modifier = Modifier.height(32.dp))

        // Pricing Cards
        PricingSelector(
            selectedPlan = selectedPlan,
            onPlanSelected = onPlanSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFFF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Purchase button
        Button(
            onClick = onPurchase,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = when (selectedPlan) {
                        PricingPlan.MONTHLY -> "Subscribe for $2.99/month"
                        PricingPlan.YEARLY -> "Subscribe for $29.99/year"
                    },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Restore button
        TextButton(onClick = onRestore, enabled = !isLoading) {
            Text("Restore Purchases", color = Color.White.copy(alpha = 0.7f))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProFeaturesList() {
    val features = listOf(
        ProFeature("🚫", "No Ads", "Enjoy uninterrupted experience"),
        ProFeature("🎨", "PRO Themes", "Access 200+ exclusive designs"),
        ProFeature("🌈", "RGB Effects", "Animated gradient keyboards"),
        ProFeature("✍️", "Custom Fonts", "Personalize your typing style"),
        ProFeature("⚡", "Priority Support", "Get help faster")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        features.forEach { feature ->
            ProFeatureItem(feature)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProFeatureItem(feature: ProFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature.icon,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feature.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = feature.description,
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PricingSelector(
    selectedPlan: PricingPlan,
    onPlanSelected: (PricingPlan) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PricingCard(
            plan = PricingPlan.YEARLY,
            isSelected = selectedPlan == PricingPlan.YEARLY,
            onClick = { onPlanSelected(PricingPlan.YEARLY) },
            badge = "BEST VALUE"
        )
        Spacer(modifier = Modifier.height(12.dp))
        PricingCard(
            plan = PricingPlan.MONTHLY,
            isSelected = selectedPlan == PricingPlan.MONTHLY,
            onClick = { onPlanSelected(PricingPlan.MONTHLY) }
        )
    }
}

@Composable
private fun PricingCard(
    plan: PricingPlan,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isSelected) Color(0xFFFFD700).copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = badge,
                                color = Color.Black,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.price,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                if (plan.pricePerMonth != null) {
                    Text(
                        text = plan.pricePerMonth,
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

data class ProFeature(
    val icon: String,
    val title: String,
    val description: String
)

enum class PricingPlan(
    val title: String,
    val price: String,
    val pricePerMonth: String?
) {
    MONTHLY("Monthly", "$2.99", null),
    YEARLY("Yearly", "$29.99", "Just $2.50/month")
}
