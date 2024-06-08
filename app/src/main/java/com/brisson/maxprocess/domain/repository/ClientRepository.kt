package com.brisson.maxprocess.domain.repository

import com.brisson.maxprocess.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun clientList(): Flow<List<Client>>
    fun searchClients(query: String): Flow<List<Client>>
    suspend fun client(id: Long): Client?
    suspend fun edit(client: Client): Long?
    suspend fun save(client: Client): Long?
    suspend fun delete(client: Client)
}
