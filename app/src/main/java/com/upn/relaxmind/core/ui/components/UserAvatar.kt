package com.upn.relaxmind.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upn.relaxmind.core.data.models.User

@Composable
fun UserAvatar(
    user: User?,
    modifier: Modifier = Modifier,
    size: Int = 40,
    fontSize: Int = 16
) {
    val context = LocalContext.current
    val avatarName = user?.avatar
    
    val resId = if (avatarName != null) {
        context.resources.getIdentifier(avatarName, "drawable", context.packageName)
    } else 0

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            // Solo aplicar fondo si no hay una imagen válida
            .then(
                if (resId == 0) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(resId),
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            InitialsFallback(user, fontSize)
        }
    }
}

@Composable
private fun InitialsFallback(user: User?, fontSize: Int) {
    val initials = buildString {
        user?.name?.firstOrNull()?.let { append(it.uppercaseChar()) }
        user?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "U" }

    Text(
        text = initials,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    )
}
