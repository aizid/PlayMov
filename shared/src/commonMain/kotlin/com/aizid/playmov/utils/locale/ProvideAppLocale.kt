package com.aizid.playmov.utils.locale

import androidx.compose.runtime.Composable

@Composable
expect fun ProvideAppLocale(language: String, content: @Composable () -> Unit)
