package com.jammes.miauau.dummy

import com.jammes.miauau.collections.PetItem
import com.jammes.miauau.core.PetsRepository
import java.util.UUID

object MockPets: PetsRepository {

    val petItemList = listOf(
        PetItem(
            id = "1",
            name = "Chaffe",
            description = "Descrição sobre o cachorro com mais de uma linha",
            age = "1 ano",
            breed = "Vira-Lata",
            sex = "Macho",
            castrated = "Castrado",
            size = "Grande",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "2",
            name = "Ada",
            description = "Descrição sobre o gato",
            age = "8 meses",
            breed = "Persa",
            sex = "Femea",
            castrated = "Não Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "3",
            name = "Snoopy",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Beagle",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Médio",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "4",
            name = "Flash",
            description = "Descrição sobre o cachorro",
            age = "2 anos",
            breed = "Pintcher",
            sex = "Macho",
            castrated = "Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "5",
            name = "Caramelo",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Vira-Lata",
            sex = "Macho",
            castrated = "Castrado",
            size = "Grande",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "6",
            name = "Tunico",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Beagle",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Médio",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "7",
            name = "Alcachofra",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Border Collie",
            sex = "Macho",
            castrated = "Castrado",
            size = "Grande",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "8",
            name = "Pingo",
            description = "Descrição sobre o cachorro",
            age = "10 meses",
            breed = "Shuaua",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "9",
            name = "Bolinha",
            description = "Descrição sobre o cachorro",
            age = "2 anos",
            breed = "Puddle",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Médio",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "10",
            name = "Snow",
            description = "Descrição sobre o gato",
            age = "1 anos",
            breed = "Siamês",
            sex = "Fêmea",
            castrated = "Não Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "11",
            name = "Rabugento",
            description = "Descrição sobre o cachorro",
            age = "5 anos",
            breed = "Sem Raça",
            sex = "Macho",
            castrated = "Castrado",
            size = "Grande",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "12",
            name = "Bug",
            description = "Descrição sobre o cachorro",
            age = "3 anos",
            breed = "Labrador",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Grande",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "13",
            name = "Muffin",
            description = "Descrição sobre o cachorro",
            age = "1 anos",
            breed = "Bulldog",
            sex = "Macho",
            castrated = "Não Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        ),
        PetItem(
            id = "14",
            name = "Chaninho",
            description = "Descrição sobre o gato",
            age = "3 anos",
            breed = "Sem Raça",
            sex = "Macho",
            castrated = "Castrado",
            size = "Pequeno",
            vaccinated = "Vacinado"
        )
    )

    override fun fetchPets(): List<PetItem> {
        return petItemList.map { it.copy() }
    }
}