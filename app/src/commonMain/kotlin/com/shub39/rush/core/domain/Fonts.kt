package com.shub39.rush.core.domain

import org.jetbrains.compose.resources.FontResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.dm_sans
import rush.app.generated.resources.figtree
import rush.app.generated.resources.inter
import rush.app.generated.resources.jost
import rush.app.generated.resources.manrope
import rush.app.generated.resources.montserrat
import rush.app.generated.resources.open_sans
import rush.app.generated.resources.outfit
import rush.app.generated.resources.poppins_regular
import rush.app.generated.resources.quicksand

enum class Fonts(
    val fullName: String,
    val font: FontResource
) {
    POPPINS("Poppins", Res.font.poppins_regular),
    DM_SANS("DM Sans", Res.font.dm_sans),
    FIGTREE("Figtree", Res.font.figtree),
    INTER("Inter", Res.font.inter),
    MANROPE("Manrope", Res.font.manrope),
    MONTSERRAT("Montserrat", Res.font.montserrat),
    OPEN_SANS("Open Sans", Res.font.open_sans),
    OUTFIT("Outfit", Res.font.outfit),
    QUICKSAND("Quicksand", Res.font.quicksand),
    JOSH("Jost", Res.font.jost)
}