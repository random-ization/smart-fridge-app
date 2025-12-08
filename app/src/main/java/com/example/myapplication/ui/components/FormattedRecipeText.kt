package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.PrimaryGreen

/**
 * A composable that parses and renders markdown-style formatted text
 * for AI-generated recipe content.
 * 
 * Supports:
 * - **Bold text** markers
 * - Section headers (lines ending with :)
 * - Numbered lists (1., 2., etc.)
 * - Bullet points (-, *, •)
 */
@Composable
fun FormattedRecipeText(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val lines = text.split("\n")
        
        lines.forEachIndexed { index, line ->
            val trimmedLine = line.trim()
            
            when {
                // Skip empty lines but add spacing
                trimmedLine.isEmpty() -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Main section headers (e.g., **Ingredients & Seasonings:**)
                trimmedLine.startsWith("**") && trimmedLine.contains(":**") -> {
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    SectionHeader(text = trimmedLine.removeSurrounding("**").removeSuffix("**"))
                }
                
                // Sub-headers or bold titles (e.g., **Dish Name:** Pasta)
                trimmedLine.startsWith("**") && trimmedLine.endsWith("**") -> {
                    BoldText(text = trimmedLine.removeSurrounding("**"))
                }
                
                // Lines with bold prefix (e.g., **Dish Name:** Some Value)
                trimmedLine.startsWith("**") && trimmedLine.contains(":**") -> {
                    KeyValueLine(text = trimmedLine)
                }
                
                // Numbered list items (e.g., 1. Step one, 2. Step two)
                trimmedLine.matches(Regex("^\\d+\\.\\s+.*")) -> {
                    val match = Regex("^(\\d+)\\.\\s+(.*)").find(trimmedLine)
                    if (match != null) {
                        val (number, content) = match.destructured
                        NumberedListItem(number = number, content = content)
                    } else {
                        RegularText(text = trimmedLine)
                    }
                }
                
                // Bullet points (-, *, •)
                trimmedLine.startsWith("-") || trimmedLine.startsWith("*") || trimmedLine.startsWith("•") -> {
                    val content = trimmedLine.removePrefix("-").removePrefix("*").removePrefix("•").trim()
                    BulletListItem(content = content)
                }
                
                // Regular text with inline bold
                else -> {
                    InlineFormattedText(text = trimmedLine)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = PrimaryGreen,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun BoldText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun KeyValueLine(text: String) {
    val parts = text.split(":**")
    if (parts.size >= 2) {
        val key = parts[0].removePrefix("**")
        val value = parts.drop(1).joinToString(":**").removeSuffix("**").trim()
        
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = "$key: ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        InlineFormattedText(text = text)
    }
}

@Composable
private fun NumberedListItem(number: String, content: String) {
    Row(
        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        InlineFormattedText(
            text = content,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BulletListItem(content: String) {
    Row(
        modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(PrimaryGreen)
        )
        Spacer(modifier = Modifier.width(12.dp))
        InlineFormattedText(
            text = content,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RegularText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
    )
}

@Composable
private fun InlineFormattedText(
    text: String,
    modifier: Modifier = Modifier
) {
    // Parse inline **bold** markers
    val annotatedString = buildAnnotatedString {
        var remaining = text
        while (remaining.isNotEmpty()) {
            val boldStart = remaining.indexOf("**")
            if (boldStart == -1) {
                append(remaining)
                break
            }
            
            // Append text before bold marker
            append(remaining.substring(0, boldStart))
            
            val afterStart = remaining.substring(boldStart + 2)
            val boldEnd = afterStart.indexOf("**")
            
            if (boldEnd == -1) {
                // No closing marker, append rest as-is
                append(remaining.substring(boldStart))
                break
            }
            
            // Append bold text
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(afterStart.substring(0, boldEnd))
            }
            
            remaining = afterStart.substring(boldEnd + 2)
        }
    }
    
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp,
        modifier = modifier
    )
}
