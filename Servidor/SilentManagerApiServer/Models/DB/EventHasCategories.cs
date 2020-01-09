using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class EventHasCategories
    {
        public int EventId { get; set; }
        public int CategoryId { get; set; }

        public virtual Category Category { get; set; }
        public virtual Event Event { get; set; }
    }
}
