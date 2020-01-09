package com.silent_manager

import com.silent_manager.g29.silent_manager.data_layer.models.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


object StaticRepo {

    fun getAcceptedInvites(): String {
        return "[\n" +
                "    {\n" +
                "        \"event\": {\n" +
                "            \"EventId\": 3,\n" +
                "            \"Name\": \"Invite 1\",\n" +
                "            \"Description\": \"New invite\",\n" +
                "            \"StartDate\": 1563069645294,\n" +
                "            \"EndDate\": 1563073245297,\n" +
                "            \"Location\": {\n" +
                "                \"eventId\": 1,\n" +
                "                \"Latitude\": 5.65,\n" +
                "                \"Longitude\": 5.65,\n" +
                "                \"Address\": \"asdfasdf\"\n" +
                "            },\n" +
                "            \"State\": 1,\n" +
                "            \"Radius\": 8\n" +
                "        },\n" +
                "        \"state\": 2,\n" +
                "        \"user\": [\n" +
                "            {\n" +
                "                \"eventId\": 1,\n" +
                "                \"name\": \"User 1\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"event\": {\n" +
                "            \"EventId\": 4,\n" +
                "            \"Name\": \"Invite 2\",\n" +
                "            \"Description\": \"New invite\",\n" +
                "            \"StartDate\": 1563069645294,\n" +
                "            \"EndDate\": 1563073245297,\n" +
                "            \"Location\": {\n" +
                "                \"eventId\": 1,\n" +
                "                \"Latitude\": 5.65,\n" +
                "                \"Longitude\": 5.65,\n" +
                "                \"Address\": \"asdfasdf\"\n" +
                "            },\n" +
                "            \"State\": 1,\n" +
                "            \"Radius\": 8\n" +
                "        },\n" +
                "        \"state\": 2,\n" +
                "        \"user\": [\n" +
                "            {\n" +
                "                \"eventId\": 1,\n" +
                "                \"name\": \"User 1\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 2,\n" +
                "                \"name\": \"User 2\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 3,\n" +
                "                \"name\": \"User 3\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 4,\n" +
                "                \"name\": \"User 4\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 5,\n" +
                "                \"name\": \"User 5\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 6,\n" +
                "                \"name\": \"User 6\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 7,\n" +
                "                \"name\": \"User 7\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 8,\n" +
                "                \"name\": \"User 8\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 9,\n" +
                "                \"name\": \"User 9\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 10,\n" +
                "                \"name\": \"User 10\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 11,\n" +
                "                \"name\": \"User 11\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"eventId\": 12,\n" +
                "                \"name\": \"User 12\",\n" +
                "                \"email\": \"sas@gmail.com\"\n" +
                "            }\n" +
                "            \n" +
                "        ]\n" +
                "    }\n" +
                "]"
    }

    fun getPendingInvites(): String {
        return "[\n" +
                "    {\n" +
                "        \"event\": {\n" +
                "            \"EventId\": 3,\n" +
                "            \"Name\": \"Invite 1\",\n" +
                "            \"Description\": \"New invite\",\n" +
                "            \"StartDate\": 1563069645294,\n" +
                "            \"EndDate\": 1563073245297,\n" +
                "            \"Location\": {\n" +
                "                \"eventId\": 1,\n" +
                "                \"Latitude\": 5.65,\n" +
                "                \"Longitude\": 5.65,\n" +
                "                \"Address\": \"asdfasdf\"\n" +
                "            },\n" +
                "            \"State\": 1,\n" +
                "            \"Radius\": 8\n" +
                "        },\n" +
                "        \"state\": 1,\n" +
                "        \"user\": []" +
                "    }\n" +
                "]"
    }


    fun getEvents(): ArrayList<Event> {
        val events: ArrayList<Event> = arrayListOf()

        val desc1 = "asdfisajdfisadfiausidf\nsidafaifiuasidfuiuisdufiasdfuiasifiasdui\nasdfasjdfiajsd"
        val desc2 = "qe3333333asdfasasdfisajdfisadfiausidf\nsidafaifiuasidfuiuisdufiasdfuiasifiasdui\nasdfasjdfiajsd"

        val location1: Location = Location(id = 1, latitude = 38.728279, longitude = -9.229863, address = "")
        val location2: Location = Location(id = 2, latitude = 38.727713, longitude = -9.227306, address = "")
        val location3: Location = Location(id = 3, latitude = 38.728303, longitude = -9.229834, address = "")

        val startDate1: Date = Date(Timestamp.valueOf("2019-06-08 19:21:00.000").time)
        val endDate1: Date = Date(Timestamp.valueOf("2020-06-11 19:40:00.000").time)

        val startDate2: Date = Date(Timestamp.valueOf("2019-05-11 22:55:00.000").time)
        val endDate2: Date = Date(Timestamp.valueOf("2019-05-11 23:35:00.000").time)

        val startDate3: Date = Date(Timestamp.valueOf("2019-6-9 16:10:00.000").time)
        val endDate3: Date = Date(Timestamp.valueOf("2019-6-10 17:35:00.000").time)

        val autor1: User = User(1, "", "")
        val autor2: User = User(2, "", "")
        val autor3: User = User(3, "", "")

        val event1: Event = Event(1, "Event1", desc1, startDate1, endDate1, autor1, location1, 100, 2, "")
        val event2: Event = Event(2, "Event2", desc2, startDate2, endDate2, autor2, location2, 100, 2, "")
        val event3: Event = Event(3, "Event3", desc2, startDate3, endDate3, autor3, location3, 200, 2, "")

        events.add(event1)
        events.add(event2)
        events.add(event3)

        return events
    }
}