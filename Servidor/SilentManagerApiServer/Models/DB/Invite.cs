using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Invite
    {
        public int UserId { get; set; }
        public int EventId { get; set; }
        public int? State { get; set; }

        public virtual Event Event { get; set; }
        public virtual InviteState StateNavigation { get; set; }
        public virtual User User { get; set; }
    }
}
