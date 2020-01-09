using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.Models.DB
{
    public class CompletedEvent
    {
        public int? EventId { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public int? Radius { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public CompletedUser Author { get; set; }
        public CompletedLocation Location { get; set; }
        public int? State { get; set; }
        public string Category { get; set; }
    }
}
