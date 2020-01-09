using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class PersonalSettings
    {
        public int? SilentMode { get; set; }
        public int UserId { get; set; }
        public string PhoneNumber { get; set; }

        public virtual Contact PhoneNumberNavigation { get; set; }
        public virtual SilentMode SilentModeNavigation { get; set; }
        public virtual User User { get; set; }
    }
}
