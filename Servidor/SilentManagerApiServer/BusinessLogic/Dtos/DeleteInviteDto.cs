using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Dtos
{
    public class DeleteInviteDto
    {
        public int InviteId { get; set; }
        public TokenInDto Token { get; set; }
    }
}
