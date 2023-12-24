package org.meatball.lunch.crm.service

import org.meatball.lunch.crm.dao.UserDao

class UserService {

    fun getLunchName(userId: Long): String? {
        return userDao.getLunchName(userId)
    }

    fun getAllUsers(): Map<Long, String> {
        return userDao.getAllUsers()
    }

    fun register(userId: Long, lunchSheetName: String): Boolean {
        return userDao.register(userId, lunchSheetName)
    }

    private companion object {
        private val userDao = UserDao()
    }
}