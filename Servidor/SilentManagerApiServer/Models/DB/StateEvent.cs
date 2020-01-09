using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class StateEvent
    {
        public StateEvent()
        {
            Event = new HashSet<Event>();
        }

        public int StateEventId { get; set; }
        public string State { get; set; }

        public virtual ICollection<Event> Event { get; set; }
    }
}
