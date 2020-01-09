﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Dtos
{
    public class EventOutDto
    {
        public int id { get; set; };
        public string Name { get; set; }
        public string Description { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public int? Author { get; set; }
        public int? Location { get; set; }
        public int? State { get; set; }
    }
}
