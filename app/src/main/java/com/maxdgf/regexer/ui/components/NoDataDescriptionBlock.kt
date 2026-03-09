package com.maxdgf.regexer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Creates no data description block.
 * @param description text for description.
 */
@Composable
fun NoDataUiDescriptionBlock(
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // description
        Text(
            text = description,
            fontWeight = FontWeight.Light
        )
    }
}