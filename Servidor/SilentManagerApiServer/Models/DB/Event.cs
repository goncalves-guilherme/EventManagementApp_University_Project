using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Event
    {
        public Event()
        {
            EventHasCategories = new HashSet<EventHasCategories>();
            Invite = new HashSet<Invite>();
        }

        public int EventId { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public int? Author { get; set; }
        public int? Location { get; set; }
        public int? State { get; set; }
        public int? Radius { get; set; }

        public virtual User AuthorNavigation { get; set; }
        public virtual Location LocationNavigation { get; set; }
        public virtual StateEvent StateNavigation { get; set; }
        public virtual ICollection<EventHasCategories> EventHasCategories { get; set; }
        public virtual ICollection<Invite> Invite { get; set; }
    }
}
