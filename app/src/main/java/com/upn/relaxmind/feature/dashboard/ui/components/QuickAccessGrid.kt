package com.upn.relaxmind.feature.dashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upn.relaxmind.core.ui.theme.*

data class QuickAccessItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val gradient: Brush,
    val bgColor: Color,
    val onClick: () -> Unit
)

@Composable
fun QuickAccessGrid(
    items: List<QuickAccessItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    QuickTile(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill space if the row is not full
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickTile(
    item: QuickAccessItem,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(150),
        label = "tileScale"
    )
    
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true),
                onClick = item.onClick
            )
            .height(140.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x12000000))
                .clip(RoundedCornerShape(24.dp))
                .background(item.bgColor)
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color.Black.copy(0.05f))), RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(brush = item.gradient),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
