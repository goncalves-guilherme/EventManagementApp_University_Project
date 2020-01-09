using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class SilentMode
    {
        public SilentMode()
        {
            PersonalSettings = new HashSet<PersonalSettings>();
        }

        public int ModeId { get; set; }
        public string Mode { get; set; }

        public virtual ICollection<PersonalSettings> PersonalSettings { get; set; }
    }
}
