using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public interface IUserRepository : IRepository<User>
    {
    }
}
