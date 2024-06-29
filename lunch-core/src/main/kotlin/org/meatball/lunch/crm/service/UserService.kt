package org.meatball.lunch.crm.service

import org.meatball.lunch.crm.dao.UserDao

class UserService {

    fun getLunchName(userId: String): String? {
        return userDao.getLunchName(userId)
    }

    fun getAllUsers(): Map<String, String> {
        return userDao.getAllUsers()
    }

    fun register(userId: String, lunchSheetName: String): Boolean {
        return userDao.register(userId, lunchSheetName)
    }

    private companion object {
        private val userDao = UserDao()
    }
}