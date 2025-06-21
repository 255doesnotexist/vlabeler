package com.sdercolin.vlabeler.ui.theme

import androidx.compose.foundation.DefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sdercolin.vlabeler.model.AppConf
import com.sdercolin.vlabeler.repository.FontRepository
import com.sdercolin.vlabeler.ui.string.*
import com.sdercolin.vlabeler.util.toColorOrNull

private val colors = darkColors(
    primary = Pink,
    primaryVariant = DarkPink,
    background = DarkGray,
    surface = Gray,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = LightGray,
    onSurface = LightGray,
    onError = Black,
)

private fun getTypography(defaultFontFamily: FontFamily?) = Typography(
    defaultFontFamily = defaultFontFamily ?: FontFamily.Default,
    button = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 1.25.sp,
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        letterSpacing = 0.sp,
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp,
    ),
)

private val AppContextMenuRepresentation = DefaultContextMenuRepresentation(
    backgroundColor = colors.surface,
    textColor = colors.onSurface,
    itemHoverColor = colors.onSurface.copy(alpha = 0.04f),
)

@Composable
fun AppTheme(viewConf: AppConf.View? = null, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLanguage provides (viewConf?.language ?: Language.default)) {
        MaterialTheme(
            colors = colors.copy(
                primary = viewConf?.accentColor?.toColorOrNull() ?: colors.primary,
                primaryVariant = viewConf?.accentColorVariant?.toColorOrNull() ?: colors.primaryVariant,
            ),
            typography = getTypography(
                FontRepository.getFontFamily(
                    viewConf?.fontFamilyName ?: AppConf.View.DEFAULT_FONT_FAMILY_NAME,
                ),
            ),
            content = {
                CompositionLocalProvider(LocalContextMenuRepresentation provides AppContextMenuRepresentation) {
                    content()
                }
            },
        )
    }
}
