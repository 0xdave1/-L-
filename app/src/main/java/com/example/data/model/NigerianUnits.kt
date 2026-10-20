package com.example.data.model

object NigerianUnits {
    val PIECE = "Piece"
    val PACK = "Pack"
    val BOTTLE = "Bottle"
    val SACHET = "Sachet"
    val KG = "Kilogram"
    val GRAM = "Gram"
    val LITRE = "Litre"
    val ML = "Millilitre"
    val CRATE = "Crate"
    val BASKET = "Basket"
    val MUDU = "Mudu"
    val BAG = "Bag"
    val BUNCH = "Bunch"
    val CARTON = "Carton"

    val standardList = listOf(
        PIECE,
        PACK,
        BOTTLE,
        SACHET,
        MUDU,
        BASKET,
        BAG,
        CRATE,
        BUNCH,
        CARTON,
        KG,
        GRAM,
        LITRE,
        ML
    )

    fun getAbbreviation(unit: String): String {
        return when (unit) {
            PIECE -> "pc"
            PACK -> "pk"
            BOTTLE -> "btl"
            SACHET -> "sach"
            KG -> "kg"
            GRAM -> "g"
            LITRE -> "L"
            ML -> "ml"
            CRATE -> "crate"
            BASKET -> "bskt"
            MUDU -> "mudu"
            BAG -> "bag"
            BUNCH -> "bnch"
            CARTON -> "ctn"
            else -> unit
        }
    }
}
