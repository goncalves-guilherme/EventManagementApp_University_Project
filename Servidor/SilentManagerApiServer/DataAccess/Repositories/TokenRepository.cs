using SilentManagerApiServer.DataAccess;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SilentManagerApiServer.Models.DB;

namespace SilentManager.API.DataAccess.Repositories
{
    public class TokenRepository : Repository<Token>, ITokenRepository
    {
        public TokenRepository(SilentManagerContext dbContext)
            : base(dbContext){ }
    }
}
