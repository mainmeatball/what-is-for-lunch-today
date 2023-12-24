package org.meatball.lunch.crm.dao

import java.util.concurrent.ConcurrentHashMap

class UserDao {

    private val users = ConcurrentHashMap<Long, String>()

    init {
        users += readUsersFromDisk()
    }

    fun getLunchName(userId: Long): String? {
        return users[userId]
    }

    fun getAllUsers(): Map<Long, String> {
        return users.toMap() // copy
    }

    fun register(userId: Long, lunchSheetName: String): Boolean {
        users[userId] = lunchSheetName
        writeUsersToDisk(users)
        return true
    }
}