using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Location
    {
        public Location()
        {
            Event = new HashSet<Event>();
        }

        public int LocationId { get; set; }
        public double? Latitude { get; set; }
        public double? Longitude { get; set; }
        public string Address { get; set; }

        public virtual ICollection<Event> Event { get; set; }
    }
}
