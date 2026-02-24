package com.example.qrgrenertor.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors

data class StepInfo(
    val icon: ImageVector,
    val label: String
)

@Composable
fun StepProgressBar(
    currentStep: Int,
    totalSteps: Int = 4,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        StepInfo(Icons.Outlined.Category, "Loại"),
        StepInfo(Icons.Outlined.Edit, "Nhập"),
        StepInfo(Icons.Outlined.Palette, "Thiết kế"),
        StepInfo(Icons.Outlined.CheckCircle, "Hoàn tất")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            StepItem(
                step = step,
                stepNumber = index + 1,
                isCompleted = index < currentStep - 1,
                isCurrent = index == currentStep - 1,
                modifier = Modifier.weight(1f)
            )

            if (index < steps.lastIndex) {
                StepConnector(
                    isCompleted = index < currentStep - 1,
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}

@Composable
private fun StepItem(
    step: StepInfo,
    stepNumber: Int,
    isCompleted: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrent) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stepScale"
    )

    val circleColor by animateColorAsState(
        targetValue = when {
            isCompleted -> QRAppColors.AccentEnd
            isCurrent -> QRAppColors.PrimaryStart
            else -> QRAppColors.DarkCardElevated
        },
        animationSpec = tween(400),
        label = "circleColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isCompleted || isCurrent -> Color.White
            else -> QRAppColors.TextTertiary
        },
        animationSpec = tween(400),
        label = "contentColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .scale(animatedScale)
                .size(40.dp)
                .then(
                    if (isCurrent) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = QRAppColors.PrimaryStart.copy(alpha = 0.4f),
                            spotColor = QRAppColors.PrimaryStart.copy(alpha = 0.4f)
                        )
                    } else Modifier
                )
                .clip(CircleShape)
                .background(
                    if (isCurrent) {
                        Brush.linearGradient(
                            colors = listOf(QRAppColors.PrimaryStart, QRAppColors.PrimaryEnd)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(circleColor, circleColor)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Hoàn thành",
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = step.icon,
                    contentDescription = step.label,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = step.label,
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun StepConnector(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "connectorFraction"
    )

    Box(
        modifier = modifier
            .height(3.dp)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(QRAppColors.DarkCardElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedFraction)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(QRAppColors.AccentEnd, QRAppColors.PrimaryStart)
                    )
                )
        )
    }
}
