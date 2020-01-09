using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public class UserRepository : Repository<User>, IUserRepository
    {

        public UserRepository(SilentManagerContext dbContext)
            : base(dbContext){}

    }
}

