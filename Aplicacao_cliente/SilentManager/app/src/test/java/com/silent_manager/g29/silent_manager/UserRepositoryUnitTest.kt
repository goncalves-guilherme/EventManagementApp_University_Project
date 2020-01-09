package com.silent_manager.g29.silent_manager

import com.silent_manager.g29.silent_manager.UnitConstants.Companion.buildUnitOfWork
import com.silent_manager.g29.silent_manager.data_layer.models.dtos.UserAuthenticationDto
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for component UserRepository
 */
class UserRepositoryUnitTest {

    /**
     * This function will be testing if the repository to get a user by a specific eventId is working properly.
     * */
    @Test
    fun testing_GetUser() {
        val unitOfWork = buildUnitOfWork()

        unitOfWork.userRepo.getUser(1)
        {
            val user = it.result
            // Checking if the user
            assertEquals(1, user?.id)
            assertEquals("Guilherme", user?.name)
            assertEquals("asdf@hotmail.com", user?.email)
        }
    }

    @Test
    fun testing_getUserByParameters(){
        val unitOfWork = buildUnitOfWork()

        val parameters = listOf(Pair("username","guil"), Pair("username","g"))

        unitOfWork.userRepo.getUsersByParameters(parameters)
        {
            val users = it.result!!
            assertEquals(1, users[0].id)
            assertEquals("Guilherme", users[0].name)
            assertEquals("gfasdf@hotmail.com", users[0].email)

            assertEquals(5, users[1].id)
            assertEquals("Guilherme3", users[1].name)
            assertEquals("gfdf@hotmail.com", users[1].email)

            assertEquals(10, users[2].id)
            assertEquals("Guilbert", users[2].name)
            assertEquals("gl@outlook.com", users[2].email)
        }
    }

    @Test
    fun testing_userLogin(){
        val unitOfWork = buildUnitOfWork()

        val user: UserAuthenticationDto =
            UserAuthenticationDto("Guilherme", "123")

        unitOfWork.userRepo.authenticateUser(user){
            val token = it.result!!

            assertEquals(token.access_token,"")
            assertEquals(token.expires,"")
            assertEquals(token.refresh_token,"")
        }
    }
}
