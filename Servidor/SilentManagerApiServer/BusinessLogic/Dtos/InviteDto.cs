using SilentManager.API.Models.DB;
using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Dtos
{
    public class InviteDto
    {
        public int UserId { get; set; }
        public int EventId { get; set; }
        public int State { get; set; }
    }

    public class InviteOutDto
    {
        public UserOutDto User { get; set; }
        public CompletedEvent Event { get; set; }
        public int? State { get; set; }
    }

    public class InviteInDto
    {
        public InviteDto Invite { get; set; }
        public TokenInDto Token { get; set; }
    }
}
