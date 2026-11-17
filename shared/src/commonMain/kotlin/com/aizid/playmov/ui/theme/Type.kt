package com.aizid.playmov.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// Brand Guideline p.11 — Primary typeface: Post Grotesk Bold, Secondary: Post Grotesk Book.
//
// Post Grotesk is a licensed font and its files are not bundled in this repo.
// To activate it:
//   1. Add the licensed files to shared/src/commonMain/composeResources/font/
//      named `post_grotesk_bold.otf` and `post_grotesk_book.otf` (or .ttf).
//   2. Uncomment the imports and playmovFontFamily() below.
//   3. Pass it into playmovTypography(fontFamily = playmovFontFamily()) in Theme.kt.
//
// import org.jetbrains.compose.resources.Font
// import playmov.shared.generated.resources.Res
// import playmov.shared.generated.resources.post_grotesk_bold
// import playmov.shared.generated.resources.post_grotesk_book
//
// @Composable
// fun playmovFontFamily() = FontFamily(
//     Font(Res.font.post_grotesk_book, FontWeight.Normal),
//     Font(Res.font.post_grotesk_bold, FontWeight.Bold)
// )

private val defaultTypography = Typography()

@Composable
fun playmovTypography(fontFamily: FontFamily = FontFamily.Default): Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Normal),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Normal),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Normal),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Normal),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Normal),
)
