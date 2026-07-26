package com.example.data.repository

import com.example.data.db.ClientProfileEntity
import com.example.data.db.CutSessionEntity
import com.example.data.db.FadeBuddyDao
import kotlinx.coroutines.flow.Flow

class FadeBuddyRepository(private val dao: FadeBuddyDao) {
    val clients: Flow<List<ClientProfileEntity>> = dao.getAllClientProfiles()
    val sessions: Flow<List<CutSessionEntity>> = dao.getAllCutSessions()

    suspend fun saveClient(client: ClientProfileEntity): Long {
        return dao.insertClientProfile(client)
    }

    suspend fun deleteClient(id: Long) {
        dao.deleteClientProfile(id)
    }

    suspend fun saveSession(session: CutSessionEntity): Long {
        return dao.insertCutSession(session)
    }

    suspend fun deleteSession(id: Long) {
        dao.deleteCutSession(id)
    }
}
