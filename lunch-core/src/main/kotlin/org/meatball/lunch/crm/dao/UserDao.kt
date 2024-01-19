package org.meatball.lunch.crm.dao

import java.util.concurrent.ConcurrentHashMap

class UserDao {

    private val users = ConcurrentHashMap<String, String>()

    init {
        users += readUsersFromDisk()
    }

    fun getLunchName(userId: String): String? {
        return users[userId]
    }

    fun getAllUsers(): Map<String, String> {
        return users.toMap() // copy
    }

    fun register(userId: String, lunchSheetName: String): Boolean {
        users[userId] = lunchSheetName
        writeUsersToDisk(users)
        return true
    }
}