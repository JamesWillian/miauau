package com.jammes.miauau.dummy

import com.jammes.miauau.collections.PetItem
import java.util.UUID

object MockPets {

    val PetItemList = listOf(
        PetItem(
            id = UUID.randomUUID().toString(),
            name = "Scooby",
            description = "Descrição sobre o cachorro",
            age = "1 ano",
            breed = "Vira-Lata",
            sex = "Macho"
        ),
        PetItem(
            id = UUID.randomUUID().toString(),
            name = "Ada",
            description = "Descrição sobre o gato",
            age = "8 meses",
            breed = "Persa",
            sex = "Femea"
        ),
        PetItem(
            id = UUID.randomUUID().toString(),
            name = "Snoopy",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Beagle",
            sex = "Macho"
        ),
        PetItem(
            id = UUID.randomUUID().toString(),
            name = "Flash",
            description = "Descrição sobre o cachorro",
            age = "2 anos",
            breed = "Pintcher",
            sex = "Macho"
        ),
    )
}