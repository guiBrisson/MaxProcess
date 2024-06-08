package com.brisson.maxprocess.data.local.repository

import com.brisson.maxprocess.data.local.dao.ClientDao
import com.brisson.maxprocess.data.local.entity.ClientEntity
import com.brisson.maxprocess.domain.model.Client
import com.brisson.maxprocess.domain.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ClientRepositoryImpl constructor(
    private val clientDao: ClientDao,
): ClientRepository {
    override fun clientList(): Flow<List<Client>> {
        return clientDao.getAll().map { it.map { entity -> entity.toDomain() } }
    }

    override fun searchClients(query: String): Flow<List<Client>> {
        return clientDao.loadSearch(query).map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun client(id: Long): Client? = withContext(Dispatchers.IO) {
        clientDao.loadById(id)?.toDomain()
    }

    override suspend fun edit(client: Client): Long? = withContext(Dispatchers.IO) {
        clientDao.upsert(client.toEntity())
    }

    override suspend fun save(client: Client): Long? = withContext(Dispatchers.IO) {
        clientDao.upsert(client.toEntity())
    }

    override suspend fun delete(client: Client) = withContext(Dispatchers.IO) {
        clientDao.delete(client.toEntity())
    }

    private fun ClientEntity.toDomain() : Client {
        return Client(id, name, createdAt, cpf, birthDate, uf, phones)
    }

    private fun Client.toEntity() : ClientEntity {
        return ClientEntity(id, name, createdAt, cpf, birthDate, uf, phones)
    }
}
