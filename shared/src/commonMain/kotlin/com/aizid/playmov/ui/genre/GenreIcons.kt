package com.aizid.playmov.ui.genre

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object GenreIcons {
    fun getIconForGenre(genreId: Int?, genreName: String?): ImageVector {
        val nameLower = genreName?.lowercase()?.trim().orEmpty()
        return when {
            genreId == 28 || nameLower.contains("action") -> Icons.Filled.FlashOn
            genreId == 12 || nameLower.contains("adventure") -> Icons.Filled.Explore
            genreId == 16 || nameLower.contains("animation") -> Icons.Filled.AutoAwesome
            genreId == 35 || nameLower.contains("comedy") -> Icons.Filled.TheaterComedy
            genreId == 80 || nameLower.contains("crime") -> Icons.Filled.Gavel
            genreId == 99 || nameLower.contains("documentary") -> Icons.Filled.Videocam
            genreId == 18 || nameLower.contains("drama") -> Icons.Filled.Psychology
            genreId == 10751 || nameLower.contains("family") -> Icons.Filled.FamilyRestroom
            genreId == 14 || nameLower.contains("fantasy") -> Icons.Filled.AutoFixHigh
            genreId == 36 || nameLower.contains("history") -> Icons.Filled.HistoryEdu
            genreId == 27 || nameLower.contains("horror") -> Icons.Filled.Visibility
            genreId == 10402 || nameLower.contains("music") -> Icons.Filled.MusicNote
            genreId == 9648 || nameLower.contains("mystery") -> Icons.Filled.Search
            genreId == 10749 || nameLower.contains("romance") -> Icons.Filled.Favorite
            genreId == 878 || nameLower.contains("sci-fi") || nameLower.contains("science fiction") -> Icons.Filled.RocketLaunch
            genreId == 10770 || nameLower.contains("tv") -> Icons.Filled.Tv
            genreId == 53 || nameLower.contains("thriller") -> Icons.Filled.Warning
            genreId == 10752 || nameLower.contains("war") -> Icons.Filled.MilitaryTech
            genreId == 37 || nameLower.contains("western") -> Icons.Filled.Landscape
            else -> Icons.Filled.Movie
        }
    }
}
