using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.DataAccess.Repositories
{
    public class InviteStateRepository : Repository<InviteState>, IInviteStateRepository
    {
        private readonly SilentManagerContext dbContext;
        public InviteStateRepository(SilentManagerContext dbContext)
            : base(dbContext)
        {
            this.dbContext = dbContext;
        }
    }
}
