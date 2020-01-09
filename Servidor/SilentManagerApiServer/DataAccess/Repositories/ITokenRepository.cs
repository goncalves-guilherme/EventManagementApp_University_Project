using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SilentManagerApiServer.Models.DB;
using SilentManagerApiServer.DataAccess;

namespace SilentManager.API.DataAccess.Repositories
{
    public interface ITokenRepository : IRepository<Token>
    {
    }

}
