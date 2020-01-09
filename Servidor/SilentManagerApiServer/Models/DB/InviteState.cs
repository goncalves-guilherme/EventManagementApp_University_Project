using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class InviteState
    {
        public InviteState()
        {
            Invite = new HashSet<Invite>();
        }

        public int InviteId { get; set; }
        public string State { get; set; }

        public virtual ICollection<Invite> Invite { get; set; }
    }
}
