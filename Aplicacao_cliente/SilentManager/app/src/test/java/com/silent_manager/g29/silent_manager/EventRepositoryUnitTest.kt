package com.silent_manager.g29.silent_manager

import org.junit.Assert
import org.junit.Test

class EventRepositoryUnitTest {

    @Test
    fun testing_GetEvents() {
        val unitOfWork = UnitConstants.buildUnitOfWork()

        val parameters = listOf(Pair("latitude","1"), Pair("longitude","1"), Pair("radius", "100"))
        unitOfWork.eventRepo.getEvents(parameters){
            it.result?.let { pageResult ->
                Assert.assertEquals(1, pageResult.currentPage)
                Assert.assertEquals(3, pageResult.pageCount)
                Assert.assertEquals(25, pageResult.pageSize)

                Assert.assertEquals(1, pageResult.results!![0].eventId)
                Assert.assertEquals("Evento 2", pageResult.results!![0].name)
                Assert.assertEquals("desc desc", pageResult.results!![0].description)
                Assert.assertEquals("Sat Apr 27 16:28:40 BST 2019", pageResult.results!![0].startDate.toString())
                Assert.assertEquals("Mon Jan 03 15:28:40 GMT 2033", pageResult.results!![0].endDate.toString())
                Assert.assertEquals(2, pageResult.results!![0].author?.id)
                Assert.assertEquals("Marco", pageResult.results!![0].author?.name)
                Assert.assertEquals("M@gmail.com", pageResult.results!![0].author?.email)
                Assert.assertEquals(1.0, pageResult.results!![0].location?.longitude)
                Assert.assertEquals(1.0, pageResult.results!![0].location?.latitude)
                Assert.assertEquals("Rua dos mares", pageResult.results!![0].location?.address)
                Assert.assertEquals(1, pageResult.results!![0].location?.id)

                Assert.assertEquals(2, pageResult.results!![1].eventId)
                Assert.assertEquals(3, pageResult.results!![1].author?.id)
                Assert.assertEquals(1, pageResult.results!![1].location?.id)

                Assert.assertEquals(3, pageResult.results!![2].eventId)
                Assert.assertEquals(1, pageResult.results!![2].author?.id)
                Assert.assertEquals(1, pageResult.results!![2].location?.id)
            }
        }
    }
}