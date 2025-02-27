package com.shub39.rush.core.domain

import androidx.annotation.FontRes
import com.shub39.rush.R

enum class Fonts(
    val fullName: String,
    @FontRes val fontId: Int
) {
    POPPINS("Poppins", R.font.poppins_regular),
    DM_SANS("DM Sans", R.font.dm_sans),
    FIGTREE("Figtree", R.font.figtree),
    INTER("Inter", R.font.inter),
    MANROPE("Manrope", R.font.manrope),
    MONTSERRAT("Montserrat", R.font.montserrat),
    OPEN_SANS("Open Sans", R.font.open_sans),
    OUTFIT("Outfit", R.font.outfit),
    QUICKSAND("Quicksand", R.font.quicksand)
}