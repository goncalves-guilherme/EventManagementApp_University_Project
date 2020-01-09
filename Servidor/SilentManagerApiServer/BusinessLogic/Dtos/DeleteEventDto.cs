using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Dtos
{
    public class DeleteEventDto
    {
        public int EventId { get; set; }
        public TokenInDto Token { get; set; }
    }
}
