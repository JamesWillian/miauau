package com.jammes.miauau.core

import com.jammes.miauau.collections.PetItem

interface PetsRepository {

    fun fetchPets():List<PetItem>
}