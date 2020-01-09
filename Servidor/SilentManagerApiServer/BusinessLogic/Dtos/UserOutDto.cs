using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Dtos
{
    public class UserOutDto
    {
        public int UserId { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
    }
}
