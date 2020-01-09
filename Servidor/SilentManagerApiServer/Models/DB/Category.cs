using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Category
    {
        public Category()
        {
            EventHasCategories = new HashSet<EventHasCategories>();
        }

        public int CategoryId { get; set; }
        public string Category1 { get; set; }

        public virtual ICollection<EventHasCategories> EventHasCategories { get; set; }
    }
}
