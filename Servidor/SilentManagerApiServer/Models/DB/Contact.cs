using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Contact
    {
        public Contact()
        {
            PersonalSettings = new HashSet<PersonalSettings>();
        }

        public string Name { get; set; }
        public string PhoneNumber { get; set; }

        public virtual ICollection<PersonalSettings> PersonalSettings { get; set; }
    }
}
