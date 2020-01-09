using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class Token
    {
        public string token { get; set; }
        public int? UserId { get; set; }
        public DateTime? Expiration_Date { get; set; }

        public virtual User User { get; set; }
    }
}
